package fr.insalyon.creatis.vip.cli.model;

/**
 * Created by qifan on 2017/6/16.
 */
public class PropertyCli {
    private String dataBasePosition;
    private String apiKey;
    private String basePath;
    private int refreshTime;
    private String enableDatabase;

    public PropertyCli(String apiKey, String dataBasePosition, String basePath, String refreshTime,String enableDatabase) {
        this.dataBasePosition = dataBasePosition;
        this.apiKey = apiKey;
        this.basePath = basePath;
        this.refreshTime = Integer.parseInt(refreshTime);
        this.enableDatabase=enableDatabase;
    }

    public String getEnableDatabase() {
        return enableDatabase;
    }

    public String getDataBasePosition() {
        return dataBasePosition;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getBasePath() {
        return basePath;
    }

    public int getRefreshTime() { return refreshTime; }
}
