# ShmamesAPI Reference

### API Keys
All API endpoints require the user call them with an API key. With Shmames, this key is added to your request URL as a
query string: `api_key=YOUR_KEY_HERE`. Keys must be created manually by the developer, and can be revoked at any time.

##### Example
An example API request would look like this:  
`GET http://shmamesapi/shmames/status?api_key=ABCDEF12345`

The above call would generate a response like this:  
```
{
  "code": 200,
  "response": {
    "status": {
      "text": "Bagpipes",
      "type": "DEFAULT"
      }
   }
}
```

## Endpoints
#### ANY - /shmames/
Ping the API and receive a basic response back.

#### GET - /shmames/status
Gets the current status of the bot on Discord.

#### POST - /shmames/status?text=NEW_STATUS_HERE&type=default
Sets the bot's status temporarily. Adjust the `text` and `type` parameters to craft the new status.

Type | Discord
--- | ---
default | "Playing"
watching | "Watching"
listening | "Listening to"