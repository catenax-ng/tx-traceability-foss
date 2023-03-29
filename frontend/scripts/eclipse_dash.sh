java -jar ../org.eclipse.dash.licenses-0.0.1-20221105.055038-599.jar yarn.lock -project automotive.tractusx -summary ../DEPENDENCIES_FRONTEND
grep -E '(restricted, #)|(restricted$)' ../DEPENDENCIES_FRONTEND | if test $(wc -l) -gt 0; then exit 1; fi
