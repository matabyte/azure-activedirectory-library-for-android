
package com.microsoft.adal.test;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;

import javax.crypto.NoSuchPaddingException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.test.mock.MockContext;
import android.test.mock.MockPackageManager;

import com.microsoft.adal.CacheKey;
import com.microsoft.adal.DefaultTokenCacheStore;
import com.microsoft.adal.ITokenCacheStore;
import com.microsoft.adal.TokenCacheItem;

public class DefaultTokenCacheStoreTests extends BaseTokenStoreTests {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ctx = this.getInstrumentation().getContext();
    }

    @Override
    protected void tearDown() throws Exception {
        
        DefaultTokenCacheStore store = new DefaultTokenCacheStore(ctx);
        store.removeAll();
        super.tearDown();
    }

    public void testGetAll() throws NoSuchAlgorithmException, NoSuchPaddingException {
        DefaultTokenCacheStore store = (DefaultTokenCacheStore)setupItems();

        Iterator<TokenCacheItem> results = store.getAll();
        assertNotNull("Iterator is supposed to be not null", results);
        TokenCacheItem item = results.next();
        assertNotNull("Has item", item);
    }

    public void testGetUniqueUsers() throws NoSuchAlgorithmException, NoSuchPaddingException {
        DefaultTokenCacheStore store = (DefaultTokenCacheStore)setupItems();
        HashSet<String> users = store.getUniqueUsersWithTokenCache();
        assertNotNull(users);
        assertEquals(2, users.size());
    }

    public void testGetTokensForResource() throws NoSuchAlgorithmException, NoSuchPaddingException {
        DefaultTokenCacheStore store = (DefaultTokenCacheStore)setupItems();

        ArrayList<TokenCacheItem> tokens = store.getTokensForResource("resource");
        assertEquals("token size", 1, tokens.size());
        assertEquals("token content", "token", tokens.get(0).getAccessToken());

        tokens = store.getTokensForResource("resource2");
        assertEquals("token size", 3, tokens.size());
    }

    public void testGetTokensForUser() throws NoSuchAlgorithmException, NoSuchPaddingException {
        DefaultTokenCacheStore store = (DefaultTokenCacheStore)setupItems();

        ArrayList<TokenCacheItem> tokens = store.getTokensForUser("userid1");
        assertEquals("token size", 2, tokens.size());

        tokens = store.getTokensForUser("userid2");
        assertEquals("token size", 2, tokens.size());
    }

    public void testExpiringTokens() throws NoSuchAlgorithmException, NoSuchPaddingException {
        DefaultTokenCacheStore store = (DefaultTokenCacheStore)setupItems();

        ArrayList<TokenCacheItem> tokens = store.getTokensForUser("userid1");
        ArrayList<TokenCacheItem> expireTokenList = store.getTokensAboutToExpire();
        assertEquals("token size", 0, expireTokenList.size());
        assertEquals("token size", 2, tokens.size());

        TokenCacheItem expire = tokens.get(0);

        Calendar timeAhead = Calendar.getInstance();
        timeAhead.add(Calendar.MINUTE, -10);
        expire.setExpiresOn(timeAhead.getTime());

        store.setItem(CacheKey.createCacheKey(expire), expire);

        expireTokenList = store.getTokensAboutToExpire();
        assertEquals("token size", 1, expireTokenList.size());
    }

    public void testClearTokensForUser() throws NoSuchAlgorithmException, NoSuchPaddingException {
        DefaultTokenCacheStore store = (DefaultTokenCacheStore)setupItems();

        store.clearTokensForUser("userid");

        ArrayList<TokenCacheItem> tokens = store.getTokensForUser("userid");
        assertEquals("token size", 0, tokens.size());

        store.clearTokensForUser("userid2");

        tokens = store.getTokensForUser("userid2");
        assertEquals("token size", 0, tokens.size());
    }

    @Override
    protected ITokenCacheStore getTokenCacheStore() throws NoSuchAlgorithmException,
            NoSuchPaddingException {
        return new DefaultTokenCacheStore(this.getInstrumentation().getTargetContext());
    }
    
     
}
