# Aggie Agenda

Aggie Agenda is a tool for UC Davis students that facilitates schedule planning. It is currently in development.

## Set Up

1. Download and unzip the repository.
2. Make sure that you have JDK properly installed and configured.
3. Make sure that you have [PostgreSQL](https://www.postgresql.org/download/) installed. In the directory in which PostgreSQL has been installed, run the following commands to start the database server in the background and create a PostgreSQL database for the project:
    ```
    $ pg_ctl -D datadir initdb
    $ pg_ctl start -D datadir -l logfile
    $ createuser -U postgres -P username
    $ createdb -U postgres -O username aggieagenda
    ```
4. Modify the necessary information in `src/PSQLConnection.java`.
5. Run the following commands to start the program:
    ```
    $ javac src/*.java -cp lib/* -d bin
    $ jar cfm AggieAgenda.jar manifest.txt bin/ -C bin 'AggieAgenda$2.class'
    $ java -jar AggieAgenda.jar
    ```

## Current and Future Features

Aggie Agenda currently provides basic functionality for displaying and choosing classes. In the future, there will be support for features such as multiple schedules, visual schedules, and further information on classes such as availability and conflicts. At the moment, it is unclear how information on currently offered classes can be acquired; it does not appear that UC Davis has a public API for this, so it may be necessary to manually retrieve class information from Schedule Builder with a script.
