package org.tfelab.data;

import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.*;
import org.tfelab.db.MongoDBAdapter;
import org.tfelab.db.PooledDataSource;
import org.tfelab.txt.StringUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class Portal {

	public static final Logger logger = LogManager.getLogger(Portal.class.getName());

	static MongoDBAdapter mongo = MongoDBAdapter.getInstance();

	static Set<String> urls = new HashSet<String>();

	static Map<String, String> tableCollectionCovnter = ImmutableMap.of(
			"essays", "org.tfelab.crawler.model.Essay",
			"sources", "org.tfelab.crawler.model.Source"
	);

	static void proc(String db, String table) {

		try {

			Connection conn = PooledDataSource.getDataSource(db).getConnection();

			Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

			stmt.setFetchSize(Integer.MIN_VALUE);

			int i = 0;

			ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);

			List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

			while (rs.next()) {

				try {

					i++;

					if (i % 10000 == 0) {
						System.err.println(table + "\t" + i);
					}

					Map<String, Object> map = new TreeMap<String, Object>();

					for (int j = 1; j <= rs.getMetaData().getColumnCount(); j++) {

						String colName = rs.getMetaData().getColumnName(j);
						if (colName.matches("feed_id|feed_task_id|template_id|create_time|fetch_time|proc_time|host_id|protocol")) {
							continue;
						}
						if (colName.matches("id")) {
							String newId = StringUtil.byteArrayToHex(StringUtil.uuid(rs.getString("url")));
							map.put(colName, newId);

						} else if (colName.matches(".+?_count")) {
							map.put(colName, rs.getInt(j));
						} else if (colName.matches(".+?_time|pubdate")) {
							map.put(colName, new Date(rs.getTimestamp(j).getTime()));
						} else if (colName.matches("src")) {
							map.put(colName, rs.getBytes(j));
						} else {
							map.put(colName, rs.getString(j));
						}
					}

					if (map.get("url") != null && String.valueOf(map.get("url")).length() > 0
							&& !urls.contains(String.valueOf(map.get("url")))) {

						if (!mongo.save("raw", tableCollectionCovnter.get(table), map)) {
							logger.error("ID: {}", map.get("id"));
						} else {
							urls.add(String.valueOf(map.get("url")));
						}

					} else {
						logger.info("Duplicate url: {}", String.valueOf(map.get("url")));
					}
				} catch (Exception e) {
					logger.error("Something wrong.", e);
				}
			}

			rs.close();
			stmt.close();
			conn.close();

		} catch (Exception e) {
			logger.error("Something wrong.", e);
		}
	}

	public static void main(String[] args) {
		//proc("raw", "essays");
		proc("raw", "sources");
	}
}
