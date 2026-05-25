package com.artmcar.rksmp6_3.domain

class LoginUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(username: String, password: String) =
        repo.login(username, password)
}

class LogoutUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke() = repo.logout()
}

class GetUsersUseCase(private val repo: UserRepository) {
    suspend operator fun invoke() = repo.getUsers()
}

class GetUserByIdUseCase(private val repo: UserRepository) {
    suspend operator fun invoke(id: Int) = repo.getUserById(id)
}
