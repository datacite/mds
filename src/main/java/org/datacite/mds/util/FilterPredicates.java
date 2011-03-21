package org.datacite.mds.util;

import org.apache.commons.collections.Predicate;
import org.datacite.mds.domain.AllocatorOrDatacentre;
import org.datacite.mds.domain.Prefix;

public class FilterPredicates {

    public static Predicate getAllocatorOrDatacentreContainsPrefixPredicate(final Prefix prefix) {
        return new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                AllocatorOrDatacentre user = (AllocatorOrDatacentre) object;
                return user.getPrefixes().contains(prefix);
            }
        };
    }

}
