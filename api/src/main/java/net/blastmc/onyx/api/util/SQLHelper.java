package net.blastmc.onyx.api.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SQLHelper {

    private ThreadPoolExecutor pool;
    private HikariDataSource source;

    public SQLHelper(String url, String user, String passwd, String database) {
        HikariConfig config;
        try {
            config = new HikariConfig();
        } catch (LinkageError ex) {
            handleLinkageError(ex);
            throw ex;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        config.setPoolName("onyx-hikari");
        config.setJdbcUrl(String.format("jdbc:mysql://%s/%s"
                , url, database));
        config.setUsername(user);
        config.setPassword(passwd);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        config.addDataSourceProperty("alwaysSendSetIsolation", "false");
        config.addDataSourceProperty("cacheCallableStmts", "true");
        config.addDataSourceProperty("useUnicode", "true");
        config.addDataSourceProperty("characterEncoding", "utf-8");
        config.addDataSourceProperty("useSSL", "false");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(10);
        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(5000);
        Log.getLogger().sendLog("§e连接 " + config.getJdbcUrl() + " 中。。。");
        this.source = new HikariDataSource(config);
        this.pool = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public Connection getConnection() throws SQLException {
        if (this.source == null) {
            throw new SQLException("Unable to get a connection from the pool. (hikari is null)");
        }
        Connection connection = this.source.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }
        return connection;
    }

    private static void handleLinkageError(LinkageError linkageError) {
        Log.getLogger().sendLog("在引入 Hikari 时发生错误 " + linkageError.getClass().getSimpleName() + "。 Onyx 似乎与其他插件冲突！");
    }

    public synchronized void create(String table, Value... values) {
        pool.execute(() -> {
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = getConnection();
                String query = Joiner.on(", ").join(Arrays.stream(values).map(value -> value.getName() + " " + value.getType().getStatement()).collect(Collectors.toList()));
                stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + "(" + query + ");");
                stmt.executeUpdate();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(stmt != null){
                        stmt.close();
                    }
                    if(conn != null){
                        conn.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public int getOrder(String table, String flag, String flagData, String data){
        CountDownLatch count = new CountDownLatch(1);
        AtomicInteger order = new AtomicInteger();
        pool.execute(new Runnable() {
            @Override
            public synchronized void run() {
                Connection conn = null;
                Statement stmt = null;
                ResultSet rs = null;
                try {
                    conn = getConnection();
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery("select data_order from (SELECT t.*, @rownum := @rownum + 1 AS data_order FROM (select * from " + table + " order by " + data + " desc) t, (SELECT @rownum := 0) r) b where " + flag + " = '" + flagData + "';");
                    if (rs.next()) {
                        order.set(rs.getInt(1));
                    }
                    rs.close();
                    stmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(rs != null){
                            rs.close();
                        }
                        if(stmt != null){
                            stmt.close();
                        }
                        if(conn != null){
                            conn.close();
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    count.countDown();
                }
            }
        });
        try {
            count.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return order.get();
    }


    public List getColumnData(String table, String flag, String flagData, int line){
        CountDownLatch count = new CountDownLatch(1);
        List<Object> list = Lists.newArrayList();
        pool.execute(() -> {
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                conn = getConnection();
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM " + table + " WHERE " + flag + " = '" + flagData + "';");
                while (rs.next()) {
                    list.add(rs.getObject(line));
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(rs != null){
                        rs.close();
                    }
                    if(stmt != null){
                        stmt.close();
                    }
                    if(conn != null){
                        conn.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                count.countDown();
            }
        });
        try {
            count.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List getRowData(String table, int line) {
        CountDownLatch count = new CountDownLatch(1);
        List<Object> list = Lists.newArrayList();
        pool.execute(() -> {
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                conn = getConnection();
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM " + table + ";");
                int j = 1;
                while (rs.next()) {
                    if (j == line) {
                        int i = 1;
                        while(true) {
                            try {
                                list.add(rs.getObject(i));
                            }catch (SQLException ex){
                                break;
                            }
                            i++;
                        }
                        break;
                    }
                    j++;
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(rs != null){
                        rs.close();
                    }
                    if(stmt != null){
                        stmt.close();
                    }
                    if(conn != null){
                        conn.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                count.countDown();
            }
        });
        try {
            count.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean checkDataExists(String table, String flag, String data){
        CountDownLatch count = new CountDownLatch(1);
        AtomicBoolean b = new AtomicBoolean(false);
        pool.execute(() -> {
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                conn = getConnection();
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table + " WHERE " + flag + " = '" + data + "';");
                if (rs.next()) {
                    b.set(rs.getInt(1) != 0);
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(rs != null){
                        rs.close();
                    }
                    if(stmt != null){
                        stmt.close();
                    }
                    if(conn != null){
                        conn.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                count.countDown();
            }
        });
        try {
            count.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return b.get();
    }

    public List getData(String query, String data){
        List<Object> list = Lists.newArrayList();
        CountDownLatch count = new CountDownLatch(1);
        pool.execute(() -> {
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                conn = getConnection();
                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
                while (rs.next()) {
                    list.add(rs.getObject(data));
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(rs != null){
                        rs.close();
                    }
                    if(stmt != null){
                        stmt.close();
                    }
                    if(conn != null){
                        conn.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                count.countDown();
            }
        });
        try {
            count.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List getData(String table, String flag, String flagData, String... datas) {
        CountDownLatch count = new CountDownLatch(1);
        List<Object> list = Lists.newArrayList();
        pool.execute(() -> {
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                conn = getConnection();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < datas.length; i++) {
                    if (i != 0) {
                        sb.append(", ");
                    }
                    sb.append(datas.clone()[i]);
                }
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT " + sb.toString() + " FROM " + table + " WHERE " + flag + " = '" + flagData + "';");
                if (rs.next()) {
                    for (int i = 0; i < datas.length; i++) {
                        Object obj = rs.getObject(i + 1);
                        list.add(obj == null ? "" : obj);
                    }
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(rs != null){
                        rs.close();
                    }
                    if(stmt != null){
                        stmt.close();
                    }
                    if(conn != null){
                        conn.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                count.countDown();
            }
        });
        try {
            count.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }

    public synchronized void putData(String table, String flag, String flagData, SqlValue... values) {
        pool.execute(() -> {
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = getConnection();
                String data = flag + ", " + Joiner.on(", ")
                        .join(Arrays.stream(values.clone())
                                .map(v -> v.data)
                                .collect(Collectors.toList()));
                String sqlValue = "'" + flagData + "', '" + Joiner.on("', '")
                        .join(Arrays.stream(values.clone())
                                .map(v -> v.value)
                                .collect(Collectors.toList())) + "'";
                String set = Joiner.on(", ")
                        .join(Arrays.stream(values.clone())
                                .map(v -> v.data + " = '" + v.value + "'")
                                .collect(Collectors.toList()));
                if (!checkDataExists(table, flag, flagData)) {
                    stmt = conn.prepareStatement("INSERT INTO " + table + " (" + data + ") VALUES(" + sqlValue + ");");
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();
                } else {
                    stmt = conn.prepareStatement("UPDATE " + table + " SET " + set + " WHERE " + flag + " = '" + flagData + "';");
                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(stmt != null){
                        stmt.close();
                    }
                    if(conn != null){
                        conn.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public synchronized void insertData(String table, SqlValue... values) {
        pool.execute(() -> {
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = getConnection();
                stmt = conn.prepareStatement(
                        "INSERT INTO " + table +
                                " (" + Joiner.on(", ").join(Arrays.stream(values).map(SqlValue::getData).collect(Collectors.toList())) + ") VALUES ('" +
                                Joiner.on("', '").join(Arrays.stream(values).map(SqlValue::getValue).collect(Collectors.toList())) + "');");
                stmt.executeUpdate();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(stmt != null){
                        stmt.close();
                    }
                    if(conn != null){
                        conn.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public int countData(String query){
        CountDownLatch count = new CountDownLatch(1);
        AtomicInteger i = new AtomicInteger(0);
        pool.execute(() -> {
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                conn = getConnection();
                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
                if (rs.next()) {
                    i.set(rs.getInt(1));
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(rs != null){
                        rs.close();
                    }
                    if(stmt != null){
                        stmt.close();
                    }
                    if(conn != null){
                        conn.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                count.countDown();
            }
        });
        try {
            count.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return i.get();
    }

    public void close(){
        this.source.close();
    }

    public List<Object> getListData(String table, String flag, String flagData, String data) {
        CountDownLatch count = new CountDownLatch(1);
        List<Object> list = Lists.newArrayList();
        pool.execute(() -> {
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                conn = getConnection();
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT " + data + " FROM " + table + " WHERE " + flag + " = '" + flagData + "';");
                while (rs.next()) {
                    list.add(rs.getObject(1));
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(rs != null){
                        rs.close();
                    }
                    if(stmt != null){
                        stmt.close();
                    }
                    if(conn != null){
                        conn.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                count.countDown();
            }
        });
        try {
            count.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list.isEmpty() ? null : list;
    }

    public static class SqlValue{

        private String data;
        private Object value;

        public SqlValue(String data, Object value) {
            this.data = data;
            this.value = value;
        }

        public String getData() {
            return data;
        }

        public Object getValue() {
            return value;
        }

    }

    public static class Value{

        private ValueType type;
        private String name;

        public Value(ValueType type, String name) {
            this.type = type;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public ValueType getType() {
            return type;
        }

    }

    public enum ValueType{

        ID,
        STRING,
        DECIMAL,
        INTEGER;

        public String getStatement(){
            switch (this){
                case ID:
                    return "INT NOT NULL AUTO_INCREMENT PRIMARY KEY";
                case STRING:
                    return "VARCHAR(200) CHARACTER SET utf8";
                case DECIMAL:
                    return "DOUBLE";
                case INTEGER:
                    return "INTEGER";
            }
            return null;
        }
    }

}
