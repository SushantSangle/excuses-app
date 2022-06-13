package xyz.sushant.excusesapp.domain.mappers

import xyz.sushant.excusesapp.base.interfaces.EntityMapper
import xyz.sushant.excusesapp.domain.dataTransfer.ExcuseDTO
import xyz.sushant.excusesapp.domain.entities.Excuse

class ExcuseMapper: EntityMapper<ExcuseDTO, Excuse> {
    override fun mapToEntity(dto: ExcuseDTO): Excuse = Excuse(dto.excuse, dto.category, dto.id)

    override fun mapFromEntity(entity: Excuse): ExcuseDTO = ExcuseDTO(entity.excuse, entity.category, entity.id)
}