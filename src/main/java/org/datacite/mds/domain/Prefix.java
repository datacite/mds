package org.datacite.mds.domain;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.datacite.mds.validation.constraints.DoiPrefix;
import org.datacite.mds.validation.constraints.Unique;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity(finders = { "findPrefixesByPrefixLike" })
@Unique(entity=Prefix.class, field="prefix")
public class Prefix {

    @NotNull
    @DoiPrefix
    private String prefix;

    @SuppressWarnings("unchecked")
    public static List<Prefix> findAllPrefixes() {
        return entityManager().createQuery("select o from Prefix o order by prefix").getResultList();
    }

    @SuppressWarnings("unchecked")
    public static List<Prefix> findPrefixEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("select o from Prefix o order by prefix").setFirstResult(firstResult).setMaxResults(
                maxResults).getResultList();
    }
}
