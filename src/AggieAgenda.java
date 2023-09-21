import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;

public class AggieAgenda {
  private static void createTables(PSQLConnection conn) {
    try {
      conn.execute(
        "CREATE TABLE IF NOT EXISTS offerings (" +
        "crn INT," +
        "subject VARCHAR(3) NOT NULL," +
        "course VARCHAR(4) NOT NULL," +
        "section VARCHAR(3) NOT NULL," +
        "professor TEXT," +
        "periods VARCHAR(25)," +
        "PRIMARY KEY (crn)," +
        "CONSTRAINT code UNIQUE (subject, course, section))"
      );
      conn.execute(
        "CREATE TABLE IF NOT EXISTS schedule (" +
        "crn INT," +
        "FOREIGN KEY (crn) REFERENCES offerings (crn))"
      );
    } catch(Exception e) { System.err.println(e.toString()); }
  }

  public static ArrayList<Class> createClassList(ResultSet classSet) {
    ArrayList<Class> classes = new ArrayList<Class>();
    try {
      while (classSet.next()) {
        int crn = classSet.getInt("crn");
        String subject = classSet.getString("subject");
        String course = classSet.getString("course");
        String section = classSet.getString("section");
        String professor = classSet.getString("professor");
        String[] periods = (String[]) classSet.getArray("periods").getArray();
        classes.add(
          new Class(crn, subject, course, section, professor, periods)
        );
      }
    } catch(Exception e) { System.err.println(e.toString()); }
    return classes;
  }

  public static JPanel createClassPanel(
    String label,
    boolean checklist,
    ResultSet classSet,
    PSQLConnection conn
  ) {
    ArrayList<Class> classes = createClassList(classSet);
    JPanel classSubPanel = new JPanel();
    for (Class currClass : classes) {
      String classStr = String.format(
        "<html>%s</html>",
        currClass.toString().replaceAll("\n", "<br />")
      );
      if (checklist) {
        JCheckBox classComponent = new JCheckBox(classStr);
        classComponent.addItemListener(new ItemListener() {
          public void itemStateChanged(ItemEvent event) {
            if (event.getStateChange() == ItemEvent.SELECTED) {
              try {
                conn.executeUpdate(
                  "INSERT INTO schedule (crn)" +
                  String.format("VALUES (%d)", currClass.getCRN())
                );
              } catch(Exception e) { System.err.println(e.toString()); }
            }

            if (event.getStateChange() == ItemEvent.DESELECTED) {
              try {
                conn.executeUpdate(
                  "DELETE FROM schedule" +
                  String.format("WHERE crn=%d", currClass.getCRN())
                );
              } catch(Exception e) { System.err.println(e.toString()); }
            }
          }
        });
        classSubPanel.add(classComponent);
      } else { classSubPanel.add(new JLabel(classStr)); }
    }

    JPanel classPanel = new JPanel();
    classPanel.setLayout(new BoxLayout(classPanel, BoxLayout.Y_AXIS));
    classPanel.add(new JLabel(label));
    classPanel.add(classSubPanel);
    return classPanel;
  }

  private static void display(PSQLConnection conn) {
    try {
      ResultSet scheduleSet = conn.executeQuery(
        "SELECT * FROM offerings JOIN schedule ON offerings.crn=schedule.crn"
        );
      ResultSet offeringSet = conn.executeQuery("SELECT * FROM offerings");
      JPanel schedulePanel = createClassPanel(
        "Schedule", false, scheduleSet, conn
      );
      JPanel offeringsPanel = createClassPanel(
        "Offerings", true, offeringSet, conn
      );
      
      JPanel container = new JPanel();
      container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
      container.add(offeringsPanel);
      container.add(schedulePanel);

      JFrame frame = new JFrame("Aggie Agenda");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.add(container);

      Rectangle bounds = GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .getMaximumWindowBounds();
      frame.setPreferredSize(new Dimension(bounds.width, bounds.height));
      container.setPreferredSize(
        new Dimension(frame.getWidth(), frame.getHeight())
      );
      offeringsPanel.setPreferredSize(
        new Dimension(container.getWidth() / 2, container.getHeight())
      );
      schedulePanel.setPreferredSize(
        new Dimension(container.getWidth() / 2, container.getHeight())
      );

      frame.pack();
      frame.setVisible(true);
    } catch(Exception e) { System.err.println(e.toString()); }
  }

  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        PSQLConnection conn = new PSQLConnection("aggieagenda");
        createTables(conn);
        display(conn);
      }
    });
  }
}
