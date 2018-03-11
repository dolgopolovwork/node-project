#Как работать с репозиторием

##Запускать тесты
```
mvn test
```
##Запускать интеграционные тесты
```
mvn test-compile failsafe:integration-test failsafe:verify
```
##Установка
```
mvn install
```
##Если сломалось на этапе анализа кода
```
mvn findbugs:gui
```
##Собирать запускаемый проект со всеми зависимостями
```
mvn clean compile assembly:single
```