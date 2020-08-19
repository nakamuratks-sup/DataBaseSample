package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DbAccess {

	// 接続先DB情報
	private String uri = "jdbc:mariadb://localhost:3306/test";

	// ユーザ名
	private String user = "****";

	// パスワード
	private String password = "****";

	private Connection conn;

	private void getDbConnection() throws SQLException {
		if (conn == null) {
			conn = DriverManager.getConnection(uri, user, password);
			conn.setAutoCommit(false);
		}
	}

	private void closeConnection() throws SQLException {
		if (conn != null) {
			conn.close();
			conn = null;
		}
	}

	private PreparedStatement getPreparedStatement(String sql) throws SQLException {
		return conn.prepareStatement(sql);
	}

	private Statement getStatement() throws SQLException {
		return conn.createStatement();
	}

	/**
	 * DB参照サンプル
	 * @throws SQLException
	 */
	public List<Product> selectDb() throws SQLException {
		// DB接続
		Connection conn = DriverManager.getConnection(uri, user, password);
		conn.setAutoCommit(false);

		// SQLステートメント取得
		Statement st = conn.createStatement();

		// SQLの実行
		String sql = "select name, price, details from product;";
		ResultSet rset = st.executeQuery(sql);

		List<Product> plist = new ArrayList<Product>();
		// 照会結果の取得
		while (rset.next()) {
			Product prd = new Product();

			System.out.print("name:" + rset.getString("name") + " ");
			System.out.print("price:" + rset.getInt("price") + " ");
			System.out.print("details:" + rset.getString("details"));
			System.out.println();

			prd.setName(rset.getString("name"));
			prd.setPrice(rset.getInt("price"));
			prd.setDetails(rset.getString("details"));

			plist.add(prd);
		}

		st.close();
		conn.close();

		return plist;
	}

	/**
	 * DB挿入サンプル
	 * @throws SQLException
	 */
	public void insertDb() throws SQLException {
		// DB接続
		Connection conn = DriverManager.getConnection(uri, user, password);
		conn.setAutoCommit(false);

		// SQLの作成
		String sql = "INSERT INTO product (name, price, details) values(?, ?, ?)";

		// SQLステートメント取得
		// try句の中で作成するとcloseを勝手にしてくれる
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			// 項目設定
			ps.setString(1, "爽健美茶");
			ps.setInt(2, 150);
			ps.setString(3, "ハトムギゲンマイプーアール");

			// SQLの実行
			ps.executeUpdate();
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			System.out.println("rollback");
			throw e;
		}

		conn.close();
	}

	public void updateDb() throws SQLException {
		// DB接続
		getDbConnection();

		// SQL作成
		String sql = "UPDATE product SET name = ?, price = ?  WHERE id = 5 ";

		// SQLステートメント取得
		try (PreparedStatement ps = getPreparedStatement(sql)) {
			ps.setString(1, "ドクペ");
			ps.setInt(2, 150);

			ps.executeUpdate();
			conn.commit();

		} catch (Exception e) {
			conn.rollback();
			System.out.println("rollback");
		}
		closeConnection();
	}

	public void deleteDb() throws SQLException {
		getDbConnection();

		String sql = "DELETE from product WHERE id = ? ";

		try (PreparedStatement ps = getPreparedStatement(sql)) {
			ps.setInt(1, 5);

			ps.executeUpdate();
			conn.commit();

		} catch (Exception e) {
			conn.rollback();
			System.out.println("rollback");
		}
		closeConnection();
	}

}
