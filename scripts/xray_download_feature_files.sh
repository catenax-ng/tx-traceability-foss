curl -u $JIRA_USERNAME:$JIRA_PASSWORD "https://jira.catena-x.net/rest/raven/1.0/export/test?filter=11645" -o features.zip
unzip -o features.zip -d cypress/e2e