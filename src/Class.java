import java.util.ArrayList;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Class {
  private int crn;
  private String subject;
  private String course;
  private String section;
  private String professor;
  private ArrayList<Period> periods;

  public Class(
    int crn,
    String subject,
    String course,
    String section,
    String professor,
    ArrayList<Period> periods
  ) {
    this.crn = crn;
    this.subject = subject;
    this.course = course;
    this.section = section;
    this.professor = professor;
    this.periods = periods;
  }
  public Class(
    int crn,
    String subject,
    String course,
    String section,
    String professor,
    String[] periods
  ) {
    this.crn = crn;
    this.subject = subject;
    this.course = course;
    this.section = section;
    this.professor = professor;
    this.periods = new ArrayList<Period>();
    for (String period : periods) this.periods.add(new Period(period));
  }

  public int getCRN() { return crn; }
  private String getTitle() {
    try {
      String url = String.format(
        "https://catalog.ucdavis.edu/courses-subject-code/%s",
        subject.toLowerCase()
      );
      Document doc = Jsoup.connect(url).get();
      Elements courseblocks = doc.getElementsByClass("courseblock");
      for (Element courseblock : courseblocks) {
        String code = courseblock.getElementsByClass("detail-code").text();
        if (code.contains(course)) {
          String title = courseblock.getElementsByClass("detail-title").text();
          return title.substring(2);
        }
      }
    } catch(Exception e) { System.err.println(e.toString()); }
    return "";
  }

  public String toString() {
    String periodStr = "";
    for (Period period : periods) periodStr += '\n' + period.toString();
    return String.format(
      "[%d] %s %s %s - %s\n%s%s",
      crn, subject, course, section, professor, getTitle(), periodStr
    );
  }
}

class Period {
  private Time start;
  private Time end;
  private char[] days;

  public Period(Time start, Time end, char[] days) {
    this.start = start;
    this.end = end;
    this.days = days;
  }
  public Period(String period) {
    int hyphen = period.indexOf('-');
    start = new Time(period.substring(0, hyphen));
    end = new Time(period.substring(hyphen));
    days = period.substring(period.lastIndexOf(' ') + 1).toCharArray();
  }

  public Time getStart() { return start; }
  public Time getEnd() { return end; }
  public char[] getDays() { return days; }

  public boolean overlaps(Period period) {
    for (char currDay : days) {
      for (char day : period.getDays()) {
        if (
          currDay == day &&
          !(start.isAfter(period.getEnd()) || end.isBefore(period.getStart()))
        ) return true;
      }
    }
    return false;
  }

  public String toString() {
    return String.format(
      "%s - %s %s",
      start.toString(), end.toString(), String.valueOf(days)
    );
  }
}

class Time {
  private int hour;
  private int minute;

  public Time(int hour, int minute) {
    this.hour = hour;
    this.minute = minute;
  }
  public Time(String time) {
    int colon = time.indexOf(':');
    hour = Integer.parseUnsignedInt(time.substring(colon - 2, colon));
    minute = Integer.parseUnsignedInt(time.substring(colon + 1, colon + 3));
    if (time.contains("PM")) hour += 12;
  }

  public int getHour() { return hour; }
  public int getMinute() { return minute; }

  public boolean isBefore(Time time) {
    return hour < time.getHour() ||
      (hour == time.getHour() && minute < this.getMinute());
  }
  public boolean isAfter(Time time) {
    return hour > time.getHour() ||
      (hour == time.getHour() && minute > this.getMinute());
  }

  public String toString() {
    String hourStr = (hour < 10 ? "0" : "") + String.format("%d", hour);
    String minuteStr = (minute < 10 ? "0" : "") + String.format("%d", minute);
    return String.format("%s:%s", hourStr, minuteStr);
  }
}
