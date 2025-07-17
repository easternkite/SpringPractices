package com.easternkite.springpractices.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController @Autowired constructor(
    private val userRepository: UserRepository
) {

    @PostMapping
    suspend fun createUser(@RequestBody request: UserCreation): ResponseEntity<User> {
        val user = User(name = request.name)
        val savedUser = userRepository.save(user)
        return ResponseEntity.ok(savedUser)
    }

    @GetMapping
    suspend fun getAllUsers(): ResponseEntity<List<User>> {
        val users = userRepository.findAll()
        return ResponseEntity.ok(users)
    }
}