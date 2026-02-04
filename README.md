# Настройка для deploy/undeploy на Tomcat

Чтобы команды `mvn tomcat7:deploy` и `mvn tomcat7:undeploy` работали, нужно настроить Tomcat и Maven

---

## 1) Tomcat: tomcat-users.xml

Открыть файл:

`TOMCAT_HOME/conf/tomcat-users.xml`

Внутри тега `<tomcat-users>...</tomcat-users>` добавить вот это:

```xml
<role rolename="manager-gui"/>
<role rolename="manager-script"/>
<role rolename="manager-jmx"/>
<role rolename="manager-status"/>
<user username="user" password="password"
      roles="manager-gui, manager-script, manager-jmx, manager-status"/>
```

---

## 2) Maven: settings.xml

Создать файл по пути:

Windows: C:\Users\<ваш_пользователь>\.m2\settings.xml  
Linux/macOS: ~/.m2/settings.xml

И вставить туда:

```xml
<settings>
  <servers>
    <server>
      <id>tomcat-local</id>
      <username>user</username>
      <password>password</password>
    </server>
  </servers>
</settings>
```