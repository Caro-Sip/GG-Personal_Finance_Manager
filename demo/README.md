# GG-Personal_Finance_Manager
An application that helps track expenses, income, and savings goals with charts and reports

Download java 17 
https://adoptium.net/download?link=https%3A%2F%2Fgithub.com%2Fadoptium%2Ftemurin17-binaries%2Freleases%2Fdownload%2Fjdk-17.0.17%252B10%2FOpenJDK17U-jdk_x64_windows_hotspot_17.0.17_10.msi&vendor=Adoptium

Download Maven
https://dlcdn.apache.org/maven/maven-3/3.9.12/binaries/apache-maven-3.9.12-bin.zip

Extract the maven zip to C:\Program Files\Apache\
create Apache Folder if there isnt one

Edit Environment variables

Add this in the JAVA_HOME
C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot

Add this in PATH
C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot\bin
C:\Program Files\Apache\apache-maven-3.9.12\bin

run this everytime u open vscode
export PATH="/c/Program Files/Apache/apache-maven-3.9.12/bin:$PATH"

run program
mvn javafx:run

