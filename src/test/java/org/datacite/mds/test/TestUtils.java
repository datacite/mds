package org.datacite.mds.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Prefix;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class TestUtils {

    public static void setUsernamePassword(String username, String password) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, password));
    }
    
    public static String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    
    public static void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public static void login(AllocatorOrDatacentre user) {
        if (user == null) {
            logout();
        } else {
            setUsernamePassword(user.getSymbol(), user.getPassword());
        }
    }

    public static Allocator createAllocator(String symbol) {
        Allocator allocator = new Allocator();
        allocator.setSymbol(symbol);
        allocator.setPassword("12345678");
        allocator.setContactEmail("dummy@example.com");
        allocator.setContactName("example contact");
        allocator.setDoiQuotaAllowed(-1);
        allocator.setDoiQuotaUsed(0);
        allocator.setIsActive(true);
        allocator.setName("example name");
        allocator.setRoleName("ROLE_ALLOCATOR");
        return allocator;
    }

    public static Datacentre createDatacentre(String symbol, Allocator allocator) {
        Datacentre datacentre = new Datacentre();
        datacentre.setSymbol(symbol);
        datacentre.setAllocator(allocator);
        datacentre.setContactEmail("dummy@example.com");
        datacentre.setContactName("example contact");
        datacentre.setDoiQuotaAllowed(-1);
        datacentre.setDoiQuotaUsed(0);
        datacentre.setDomains("example.com");
        datacentre.setIsActive(true);
        datacentre.setName("example name");
        datacentre.setRoleName("ROLE_DATACENTRE");
        return datacentre;
    }

    public static Dataset createDataset(String doi, Datacentre datacentre) {
        Dataset dataset = new Dataset();
        dataset.setDoi(doi);
        dataset.setDatacentre(datacentre);
        return dataset;
    }

    public static Prefix createPrefix(String prefix) {
        Prefix prefixObj = new Prefix();
        prefixObj.setPrefix(prefix);
        return prefixObj;
    }

    public static Set<Prefix> createPrefixes(String... prefixes) {
        Set<Prefix> prefixSet = new HashSet<Prefix>();
        if (prefixes != null) {
            for (String prefix : prefixes) {
                prefixSet.add(createPrefix(prefix));
            }
        }
        return prefixSet;
    }

	public static byte[] getTestMetadata() throws IOException {
		InputStream in = TestUtils.class.getClassLoader().getResourceAsStream(
				"datacite-metadata-sample-v2.0.xml");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[1024];
			int n;
			while ((n = in.read(buffer)) != -1) {
				out.write(buffer, 0, n);
			}

			return out.toByteArray();
		} finally {
			in.close();
			out.close();
		}
	}
}
