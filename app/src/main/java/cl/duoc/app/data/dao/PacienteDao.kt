package cl.duoc.app.data.dao

import androidx.room.*
import cl.duoc.app.data.entity.Paciente
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones con la tabla Pacientes
 */
@Dao
interface PacienteDao {
    
    @Query("SELECT * FROM pacientes ORDER BY apellido, nombre ASC")
    fun getAllPacientes(): Flow<List<Paciente>>
    
    @Query("SELECT * FROM pacientes WHERE activo = 1 ORDER BY apellido, nombre ASC")
    fun getPacientesActivos(): Flow<List<Paciente>>
    
    @Query("SELECT * FROM pacientes WHERE id = :id")
    suspend fun getPacienteById(id: Long): Paciente?
    
    @Query("SELECT * FROM pacientes WHERE id = :id")
    fun getPacienteByIdFlow(id: Long): Flow<Paciente?>
    
    @Query("SELECT * FROM pacientes WHERE rut = :rut")
    suspend fun getPacienteByRut(rut: String): Paciente?
    
    @Query("SELECT * FROM pacientes WHERE email = :email")
    suspend fun getPacienteByEmail(email: String): Paciente?
    
    @Query("SELECT * FROM pacientes WHERE nombre LIKE '%' || :query || '%' OR apellido LIKE '%' || :query || '%' OR rut LIKE '%' || :query || '%'")
    fun searchPacientes(query: String): Flow<List<Paciente>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(paciente: Paciente): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pacientes: List<Paciente>)
    
    @Update
    suspend fun update(paciente: Paciente)
    
    @Delete
    suspend fun delete(paciente: Paciente)
    
    @Query("DELETE FROM pacientes WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM pacientes")
    suspend fun deleteAll()
}
