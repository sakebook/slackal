# Slackal
Show google calendar from Slack

![image](https://raw.githubusercontent.com/sakebook/slackal/master/art/image.png)

## Requirement
- Slack slash command
- Service Account Client (Google APIs)
- API Access (Google Apps Admin Console)


## Usage
- Use slack command in slack
  - Example: `/slackal`

## Build
### Environment variables
|Key|Require|Description|Example|
|:---|:---:|:---:|:---|
|ACCOUNT_USER|Yes|Google Apps Account|example@example.com|
|CALENDAR_IDS|Yes|Google Calendar Id|example.com_XXXXX@resource.calendar.google.com,example.com_XXXXX@resource.calendar.google.com|
|CLIENT_SECRET|Yes|OAuth 2.0 Client ID credentials|{ "type": "service_account","project_id": "XXXX","private_key_id": "XXXX",...}|
|GRADLE_TASK|No|Use deploy binary for Heroku|installDist|

```bash
$ ./gradlew run
```
Access : http://localhost:8080/

Slash command endpoint : http://localhost:8080/slackal

## Deploy
### Binary
```bash
$ ./gradlew installDist
```

Directory

```bash
./build/install/slackal/bin
```
### Docker
Use jib

```bash
$ ./gradlew jibDockerBuild
```
