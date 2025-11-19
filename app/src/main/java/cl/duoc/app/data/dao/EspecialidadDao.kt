package cl.duoc.app.data.dao

import androidx.room.*
import cl.duoc.app.data.entity.Especialidad
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones con la tabla Especialidades
 */
@Dao
interface EspecialidadDao {
    
    @Query("SELECT * FROM especialidades ORDER BY nombre ASC")
    fun getAllEspecialidades(): Flow<List<Especialidad>>
    
    @Query("SELECT * FROM especialidades WHERE activa = 1 ORDER BY nombre ASC")
    fun getEspecialidadesActivas(): Flow<List<Especialidad>>
    
    @Query("SELECT * FROM especialidades WHERE id = :id")
    suspend fun getEspecialidadById(id: Long): Especialidad?
    
    @Query("SELECT * FROM especialidades WHERE id = :id")
    fun getEspecialidadByIdFlow(id: Long): Flow<Especialidad?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(especialidad: Especialidad): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(especialidades: List<Especialidad>)
    
    @Update
    suspend fun update(especialidad: Especialidad)
    
    @Delete
    suspend fun delete(especialidad: Especialidad)
    
    @Query("DELETE FROM especialidades WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM especialidades")
    suspend fun deleteAll()
}
