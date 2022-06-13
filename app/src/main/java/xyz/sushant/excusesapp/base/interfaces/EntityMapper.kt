package xyz.sushant.excusesapp.base.interfaces

interface EntityMapper<D: Any, E: Any> {
    fun mapToEntity(dto: D) : E
    fun mapFromEntity(entity: E) : D
}