

## MySQL 설치 & 실행
```sh
arch -arm64 brew install mysql
arch -arm64 brew services start mysql
```

## DB 계정 만들기 
```mysql
CREATE DATABASE sbexample;
CREATE USER 'sbuser'@'localhost' IDENTIFIED BY 'sbpass';
GRANT ALL PRIVILEGES ON sbexample.* TO 'sbuser'@'localhost';
FLUSH PRIVILEGES;
```

## Gradle 의존성 추가
```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("mysql:mysql-connector-j:8.3.0") // 또는 9.x
}
```

## resources/application.properties 설정
```
spring.datasource.url=jdbc:mysql://localhost:3306/sbexample  
spring.datasource.username=easternkite  
spring.datasource.password=sbpass  
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver  
  
spring.jpa.hibernate.ddl-auto=update  
spring.jpa.show-sql=true  
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

## JPA - User Entity 생성
```kotlin
@Entity
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String
)

interface UserRepository : JpaRepository<User, Long>
```

## REST API  Controller 생성 
```kotlin
@RestController  
@RequestMapping("/users")  
class UserController @Autowired constructor(  
    private val userRepository: UserRepository  
) {  
  
    @PostMapping  
    suspend fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<User> {  
        val user = User(id = 0, name = request.name)  
        val savedUser = userRepository.save(user)  
        return ResponseEntity.ok(savedUser)  
    }  
  
    @GetMapping  
    suspend fun getAllUsers(): ResponseEntity<List<User>> {  
        val users = userRepository.findAll()  
        return ResponseEntity.ok(users)  
    }  
}
```

## Coroutines 를 사용하려면? 
다음 의존성을 추가해야한다.
```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2") // 코루틴 라이브러리  
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.2") // 코루틴 reactor 라이브러리
}
```

이렇게 해도 SpringBoot는 기본적으로 blocking io operation 기반이라, 코루틴을 사용하는 장점이 없다.

이걸 보완하려면? WebFlux?라는 녀석을 사용해야한다고한다.?
