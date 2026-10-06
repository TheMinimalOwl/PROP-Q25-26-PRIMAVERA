# Important gradle commands
*./gradlew run*: will run your application in the environment. This is useful to test your application in the development environment.<br>
*./gradlew jar*: will create a fat jar inside the directory ../EXE/ with the project code + dependencies.<br>
*./gradlew clean*: will clean the compilation files and the created artifacts.<br>
*./gradlew javadoc*: will generate the corresponding javadoc.<br>

*./gradlew test*: will run your unit tests.<br>
*./gradlew assembleDist*: will create a .tar and a .zip (both contain the same) in the directory <project root>/build/distributions that contain the whole directory structure that will allow to install your project along with its dependencies in a machine without IDE (only with java 11 installed) and run it.<br>
*./gradlew :dependencies*: to view the dependency tree.<br>

### More info
- [Gradle application plugin](https://docs.gradle.org/current/userguide/application_plugin.html)
- [Gradle Getting Started](https://docs.gradle.org/current/userguide/getting_started.html)
- [Gradle tutorial extra](https://tomgregory.com/gradle/gradle-tutorial-for-complete-beginners/)
