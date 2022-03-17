package me.moty.parkour;

import java.util.concurrent.locks.ReentrantLock;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ParkourSQL {
	private NFTParkour m;
	public HikariDataSource ds;
	private final ReentrantLock lock = new ReentrantLock();

	public ParkourSQL(NFTParkour m) {
		this.m = m;
		init();
		setup();
	}

	private void init() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://" + this.m.host + ":" + this.m.port + "/" + this.m.database + "?useSSL="
				+ this.m.ssl + "&useUnicode=true&autoReconnect=true&testOnBorrow=true&characterEncoding=UTF-8");
		config.setUsername(this.m.username);
		config.setPassword(this.m.password);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "400");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		ds = new HikariDataSource(config);
	}

	public void setup() {
		try {
			this.lock.lock();

		} finally {
			this.lock.unlock();
		}
	}
}
