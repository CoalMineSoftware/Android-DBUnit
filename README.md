# Android DBUnit

This library is a wrapper that allows Android developers to write [DBUnit](http://dbunit.sourceforge.net/) test cases against their SQLite-based database code.

It is loosely based on work uPhyca Inc. did in its deprecated [AndroidJUnit4](https://github.com/esmasui/AndroidJUnit4) project, which included Android ports of some JUnit 3-based libraries.

This project relies on [Christian Werner](http://ch-werner.de/)'s JDBC2z SQLite JDBC driver implementation, which is also distributed as [component of Android](https://android.googlesource.com/platform/external/javasqlite/). Because JDBC2z does not seem to be well documented and it is unclear how well it is maintained, it would be nice to replace it with a different JDBC implementation. However, there don't seem to be any suitable alternatives. For example, Xerial's [SQLite-JDBC](https://bitbucket.org/xerial/sqlite-jdbc) implementation seems to be the most robust but it does not seem to support Android (or at least common architectures like ARM.) [SQLDroid](https://github.com/SQLDroid/SQLDroid) is another alternative but it causes DBUnit to throw some unexpected Exceptions.
