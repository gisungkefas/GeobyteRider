package kefas.backend.GeoByte.config;

import lombok.RequiredArgsConstructor;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class LocalMemStorage {

    private final String BLACK_LIST = "Blacklist";
    private final MemcachedClient memcachedClient;
    Logger logger = LoggerFactory.getLogger(LocalMemStorage.class);

    public void save(String key,String value, int expiryInSeconds) {

        try {

            memcachedClient.set(key, expiryInSeconds, value);

        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            e.printStackTrace();
            logger.info("Memcached encountered an error : {}",e.getMessage());
        }

    }

    public void setBlacklist(String email, String token, int expiryInSeconds) {
        String key = BLACK_LIST + email;
        try {
            String blackListedToken = "";
            if (keyExist(key)) {
                blackListedToken = getValueByKey(key);
            }

            memcachedClient.set(key, expiryInSeconds, blackListedToken + token + ",");
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            e.printStackTrace();
            logger.info("Memcached encountered an error : {}", e.getMessage());
        }
    }

    public boolean isTokenBlackListed(String email, String token) {
        String key = BLACK_LIST + email;
        if (keyExist(key)) {
            String existingToken = getValueByKey(key);
            String[] tokens = existingToken.split(",");
            for (String cachedToken: tokens) {
                if (cachedToken.equals(token)) return true;
            }
        }
        return false;
    }

    public String getValueByKey(String key) {
        try {
            return memcachedClient.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean keyExist(String key) {
        try {
            return memcachedClient.get(key)!=null;
        } catch (Exception e) {
            return null;
        }
    }

    public void clear(String key) {
        try {
            memcachedClient.delete(key);
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            e.printStackTrace();
            logger.info("Memcached encountered an error : {}",e.getMessage());
        }
    }
}
