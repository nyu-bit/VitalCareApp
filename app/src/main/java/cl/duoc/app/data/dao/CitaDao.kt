package cl.duoc.app.data.dao

import androidx.room.*
import cl.duoc.app.data.entity.Cita
import cl.duoc.app.data.entity.EstadoCita
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones con la tabla Citas
 */
@Dao
interface CitaDao {
    
    @Query("SELECT * FROM citas ORDER BY fecha DESC, hora DESC")
    fun getAllCitas(): Flow<List<Cita>>
    
    @Query("SELECT * FROM citas WHERE id = :id")
    suspend fun getCitaById(id: Long): Cita?
    
    @Query("SELECT * FROM citas WHERE id = :id")
    fun getCitaByIdFlow(id: Long): Flow<Cita?>
    
    @Query("SELECT * FROM citas WHERE pacienteId = :pacienteId ORDER BY fecha DESC, hora DESC")
    fun getCitasByPaciente(pacienteId: Long): Flow<List<Cita>>
    
    @Query("SELECT * FROM citas WHERE especialidadId = :especialidadId ORDER BY fecha DESC, hora DESC")
    fun getCitasByEspecialidad(especialidadId: Long): Flow<List<Cita>>
    
    @Query("SELECT * FROM citas WHERE estado = :estado ORDER BY fecha DESC, hora DESC")
    fun getCitasByEstado(estado: EstadoCita): Flow<List<Cita>>
    
    @Query("SELECT * FROM citas WHERE fecha = :fecha ORDER BY hora ASC")
    fun getCitasByFecha(fecha: String): Flow<List<Cita>>
    
    @Query("SELECT * FROM citas WHERE fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY fecha ASC, hora ASC")
    fun getCitasByRangoFechas(fechaInicio: String, fechaFin: String): Flow<List<Cita>>
    
    @Query("""
        SELECT * FROM citas 
        WHERE pacienteId = :pacienteId 
        AND estado IN (:estados)
        ORDER BY fecha DESC, hora DESC
    """)
    fun getCitasByPacienteAndEstados(pacienteId: Long, estados: List<EstadoCita>): Flow<List<Cita>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cita: Cita): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(citas: List<Cita>)
    
    @Update
    suspend fun update(cita: Cita)
    
    @Delete
    suspend fun delete(cita: Cita)
    
    @Query("DELETE FROM citas WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM citas")
    suspend fun deleteAll()
    
    @Query("UPDATE citas SET estado = :nuevoEstado WHERE id = :citaId")
    suspend fun updateEstado(citaId: Long, nuevoEstado: EstadoCita)
}
