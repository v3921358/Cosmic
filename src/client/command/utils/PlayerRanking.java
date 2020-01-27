package client.command.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ScheduledFuture;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import server.TimerManager;
import tools.DatabaseConnection;

public class PlayerRanking {
	public class Rank {
		private int rank;
		private int id;
		private String name;
		private int level;

		private Rank(int rank, int id, String name, int level) {
			this.rank = rank;
			this.id = id;
			this.name = name;
			this.level = level;
		}

		public int getRank() {
			return rank;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public int getLevel() {
			return level;
		}
	}

	private static PlayerRanking instance = null;
	private static String RANK_QUERY = "SELECT `characters`.`id`, `characters`.`name`, `characters`.`level` FROM `characters` LEFT JOIN accounts ON accounts.id = characters.accountid WHERE `characters`.`gm` = '0' AND `accounts`.`banned` = '0' ORDER BY level DESC, exp DESC LIMIT 50";
	private static int REFRESH_INTERVAL = 60 * 60 * 1000;

	private List<Rank> ranking;
	private ScheduledFuture<?> worker;

	private PlayerRanking() {
		this.ranking = new ArrayList<Rank>();
		updateRanks();

		this.worker = TimerManager.getInstance().register(new Runnable() {
                    @Override
                    public void run() {
                    	updateRanks();
                    }
                }, REFRESH_INTERVAL, REFRESH_INTERVAL);
	}

	public static PlayerRanking getInstance() {
		if(instance == null) {
			instance = new PlayerRanking();
		}
		return instance;
	}

	public List<Rank> getRanking() {
		return Collections.unmodifiableList(this.ranking);
	}

	private boolean updateRanks() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection con = null;
		int curRank = 1;
		List<Rank> newRanking = new ArrayList<Rank>();

		try {
			con = DatabaseConnection.getConnection();
			ps = con.prepareStatement(RANK_QUERY);
			rs = ps.executeQuery();

			while(rs.next()) {
				Rank player = new Rank(curRank, rs.getInt("id"), rs.getString("name"), rs.getInt("level"));
				newRanking.add(player);
				curRank++;
			}

			ps.close();
			rs.close();
			con.close();
        } catch (SQLException ex) {
        	ex.printStackTrace();
        	try {
        		if(con != null && !con.isClosed()) {
        			con.close();
	        	}
	        	if(ps != null && !ps.isClosed()) {
	        		ps.close();
	        	}
	        	if(rs != null && !rs.isClosed()) {
	        		rs.close();
	        	}
        	} catch (SQLException ex2) {
        		ex2.printStackTrace();
        	}
        	return false;
        }

        this.ranking = new ArrayList<Rank>(newRanking);
        return true;
	}
}