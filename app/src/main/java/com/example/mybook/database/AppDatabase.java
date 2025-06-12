package com.example.mybook.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Book.class, ReadingProgress.class, User.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "mybook_db";
    private static AppDatabase instance;

    public abstract BookDao bookDao();
    public abstract ReadingProgressDao readingProgressDao();
    public abstract UserDao userDao();

    // 定义从版本1到版本2的迁移
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // 创建带索引的临时表
            database.execSQL("CREATE TABLE IF NOT EXISTS `reading_progress_new` " +
                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`bookId` INTEGER NOT NULL, " +
                    "`pagesRead` INTEGER NOT NULL, " +
                    "`date` INTEGER NOT NULL, " +
                    "FOREIGN KEY(`bookId`) REFERENCES `books`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)");
            
            // 创建索引
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_reading_progress_new_bookId` ON `reading_progress_new` (`bookId`)");
            
            // 复制数据
            database.execSQL("INSERT INTO reading_progress_new (id, bookId, pagesRead, date) " +
                    "SELECT id, bookId, pagesRead, date FROM reading_progress");
            
            // 删除旧表
            database.execSQL("DROP TABLE reading_progress");
            
            // 重命名新表
            database.execSQL("ALTER TABLE reading_progress_new RENAME TO reading_progress");
        }
    };
    
    // 定义从版本2到版本3的迁移
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // 添加email列到users表
            database.execSQL("ALTER TABLE users ADD COLUMN email TEXT");
            
            // 为现有用户设置默认邮箱
            database.execSQL("UPDATE users SET email = username || '@example.com' WHERE email IS NULL");
        }
    };

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME)
                    .allowMainThreadQueries() // 仅用于简单演示，实际应用中应使用异步操作
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // 添加迁移策略
                    // 如果迁移失败，可以使用下面的方法（会丢失数据）
                    // .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
    
    // 用于开发阶段重置数据库的方法
    public static synchronized AppDatabase getDevInstance(Context context) {
        instance = null; // 清除旧实例
        instance = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration() // 使用破坏性迁移（会丢失数据）
                .build();
        return instance;
    }
} 