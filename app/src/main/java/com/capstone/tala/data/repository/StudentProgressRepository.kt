package com.capstone.tala.data.repository

import com.capstone.tala.data.db.ActivityResultDao
import com.capstone.tala.data.db.ActivityResultEntity
import com.capstone.tala.data.db.AvatarItemOwnedDao
import com.capstone.tala.data.db.AvatarItemOwnedEntity
import com.capstone.tala.data.db.BadgeEarnedDao
import com.capstone.tala.data.db.BadgeEarnedEntity
import com.capstone.tala.data.db.ItemMasteryDao
import com.capstone.tala.data.db.ItemMasteryEntity
import com.capstone.tala.data.db.LevelProgressDao
import com.capstone.tala.data.db.LevelProgressEntity
import com.capstone.tala.data.db.PlaySessionDao
import com.capstone.tala.data.db.PlaySessionEntity
import com.capstone.tala.data.db.QuestAttemptDao
import com.capstone.tala.data.db.QuestAttemptEntity
import com.capstone.tala.data.db.StarTransactionDao
import com.capstone.tala.data.db.StarTransactionEntity
import com.capstone.tala.data.db.StarWalletDao
import com.capstone.tala.data.db.StarWalletEntity
import com.capstone.tala.data.db.StudentDao
import com.capstone.tala.data.db.StudentEntity
import com.capstone.tala.data.db.StudentSettingsDao
import com.capstone.tala.data.db.StudentSettingsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StudentProgressRepository @Inject constructor(
    private val studentDao: StudentDao,
    private val levelProgressDao: LevelProgressDao,
    private val activityResultDao: ActivityResultDao,
    private val questAttemptDao: QuestAttemptDao,
    private val starWalletDao: StarWalletDao,
    private val starTransactionDao: StarTransactionDao,
    private val avatarItemOwnedDao: AvatarItemOwnedDao,
    private val badgeEarnedDao: BadgeEarnedDao,
    private val itemMasteryDao: ItemMasteryDao,
    private val studentSettingsDao: StudentSettingsDao,
    private val playSessionDao: PlaySessionDao
) {
    fun getStudents(): Flow<List<StudentEntity>> = studentDao.getAllStudents()

    fun getStudent(studentId: String): Flow<StudentEntity?> = studentDao.getStudentById(studentId)

    suspend fun saveStudent(student: StudentEntity) = studentDao.upsertStudent(student)

    fun getProgressForStudent(studentId: String): Flow<List<LevelProgressEntity>> =
        levelProgressDao.getProgressForStudent(studentId)

    fun getModuleProgress(studentId: String, moduleId: String): Flow<List<LevelProgressEntity>> =
        levelProgressDao.getModuleProgress(studentId, moduleId)

    suspend fun saveLevelProgress(progress: LevelProgressEntity) =
        levelProgressDao.upsertLevelProgress(progress)

    suspend fun getLevelProgress(studentId: String, moduleId: String, levelIndex: Int): LevelProgressEntity? =
        levelProgressDao.getLevelProgress(studentId, moduleId, levelIndex)

    suspend fun saveActivityResult(result: ActivityResultEntity) =
        activityResultDao.upsertActivityResult(result)

    fun getActivityResults(studentId: String): Flow<List<ActivityResultEntity>> =
        activityResultDao.getResultsByStudent(studentId)

    suspend fun saveQuestAttempt(attempt: QuestAttemptEntity) =
        questAttemptDao.upsertAttempt(attempt)

    fun getQuestAttempts(studentId: String): Flow<List<QuestAttemptEntity>> =
        questAttemptDao.getAttemptsForStudent(studentId)

    suspend fun saveWallet(wallet: StarWalletEntity) = starWalletDao.upsertWallet(wallet)

    suspend fun getWallet(studentId: String): StarWalletEntity? = starWalletDao.getWalletForStudent(studentId)

    fun observeWallet(studentId: String): Flow<StarWalletEntity?> = starWalletDao.observeWalletForStudent(studentId)

    suspend fun saveTransaction(transaction: StarTransactionEntity) =
        starTransactionDao.upsertTransaction(transaction)

    fun getTransactions(studentId: String): Flow<List<StarTransactionEntity>> =
        starTransactionDao.getTransactionsForStudent(studentId)

    suspend fun saveOwnedItem(item: AvatarItemOwnedEntity) = avatarItemOwnedDao.upsertItem(item)

    fun getOwnedItems(studentId: String): Flow<List<AvatarItemOwnedEntity>> =
        avatarItemOwnedDao.getItemsForStudent(studentId)

    suspend fun saveBadge(badge: BadgeEarnedEntity) = badgeEarnedDao.upsertBadge(badge)

    fun getBadges(studentId: String): Flow<List<BadgeEarnedEntity>> = badgeEarnedDao.getBadgesForStudent(studentId)

    suspend fun saveMastery(item: ItemMasteryEntity) = itemMasteryDao.upsertMastery(item)

    fun getMastery(studentId: String): Flow<List<ItemMasteryEntity>> = itemMasteryDao.getMasteryForStudent(studentId)

    suspend fun saveSettings(settings: StudentSettingsEntity) = studentSettingsDao.upsertSettings(settings)

    fun getSettings(studentId: String): Flow<List<StudentSettingsEntity>> =
        studentSettingsDao.getSettingsForStudent(studentId)

    suspend fun saveSession(session: PlaySessionEntity) = playSessionDao.upsertSession(session)

    fun getSessions(studentId: String): Flow<List<PlaySessionEntity>> =
        playSessionDao.getSessionsForStudent(studentId)
}
