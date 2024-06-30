# Money service
Сервис по сбору, хранению и предоставлению информации о финансовых затратах пользователей на разные категории.

## Docker compose
#### Для запуска приложения в Docker нужно:
- Скачать и установить Docker и Docker Compose.
- Построить проект: `./gradlew build`
- Собрать образ **money-service**: `docker image build -t money-service:money-service .`
- Собрать образ **money-producer**: `docker image build -t money-producer:money-producer ./money-producer`
- Запустить контейнеры: `docker-compose up`

Service	| Image | Ports	| Description
------------- | --------------------| ------------ | ------------------- |
main	| money-service	| 8080:8080 | Сервис с основным приложением |
db | postgres:14 | - | Сервис базы даных postgres 14 версии |
money-producer | money-producer | - | Сервис для продуцирования сообщений в Kafka |
zookeeper | confluentinc/cp-zookeeper:7.2.1 | 2181:2181 | Сервис zookeeper |
kafka | confluentinc/cp-server:7.2.1 | 9092:9092 <br> 9997:9997 | Сервис kafka |
kafka-ui | provectuslabs/kafka-ui:latest | 8082:8080 | Сервис kafka-ui |

## API
**Endpoints:**
- http://localhost:8080/swagger-ui/index.html - Swagger-ui
- http://localhost:8082/ - Kafka-ui

Method	| Path	| Description	|
------------- | ------------------------- | ------------- |
GET	| /users	| Возвращает список пользователей UserDto	|
GET | /users/{id} | Получение пользователя по id |
GET | /users/{id}/money_costs | Получение списка денежных затрат пользовтеля за определенный день. Поддержана пагинация и фильтрация по категориям | 
POST	| /users	| Создание пользователей. Поле email - обязательное, name - необязательное |
DELETE | /users | Удаление пользователя по id |