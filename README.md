# spring-boot-admin

Example of `docker-compose.yml`

```yaml
version: "3"
services:
  app:
    image: 'ghcr.io/horyu1234/spring-boot-admin:master'
    restart: always
    ports:
      # Public HTTP Port:
      - 'CHANGE_THIS_TO_YOUR_HTTP_PORT:80'
    volumes:
      - CHANGE_THIS_TO_YOUR_SETTING_PATH/application.yml:/application.yml
```

Example of `application.yml`

```yaml
spring:
  security:
    user:
      name: <Admin Page ID>
      password: <Admin Page PW>
```

Example of `application.yml` with Discord WebHook

```yaml
spring:
  security:
    user:
      name: <Admin Page ID>
      password: <Admin Page PW>
  boot:
    admin:
      notify:
        discord:
          webhook-url: <WebHook URL>
          message: "*#{instance.registration.name}* (#{instance.id}) Instance status changed to *#{event.statusInfo.status}*"
```