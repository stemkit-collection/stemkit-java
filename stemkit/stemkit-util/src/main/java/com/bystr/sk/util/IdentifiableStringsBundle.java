/*  vim: set sw=4:
 *  Copyright (c) 2018, Gennady Bystritsky <gennady@bystr.com>
 *
 *  Distributed under the MIT License.
 *  This is free software. See 'LICENSE' for details.
 *  You must read and accept the license prior to use.
*/

package com.bystr.sk.util;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a data type consisting of a set of strings and a string id.
 * Its instances can be serialized into and composed from JSON.
 * <p>
 * @author Gennady Bystritsky (<a href="mailto:gennady@bystr.com">gennady@bystr.com</a>)
*/
@JsonPropertyOrder({
    IdentifiableStringsBundle.ID,
    IdentifiableStringsBundle.STRINGS,
})
public class IdentifiableStringsBundle {
    static final String ID = "id";
    static final String STRINGS = "strings";

    private final String _id;
    private final Set<String> _strings = new HashSet<>();

    /**
     * Constructs an identifiable string bundle with a specified id
     * and empty set of strings.
     * <p>
     * @param id [{@link String}]
     *     the id for this set of strings
    */
    public IdentifiableStringsBundle(final String id) {
        this(id, null);
    }

    /**
     * Constructs an identifiable string bundle with a specified id
     * and specified set of strings.
     * <p>
     * @param id [{@link String}]
     *     the id for this set of strings
     *
     * @param strings [{@link Set}<{@link String}>]
     *     the set of strings to keep in this identifiable string bundle
    */
    @JsonCreator
    public IdentifiableStringsBundle(@JsonProperty(ID) final String id, @JsonProperty(STRINGS) final Set<String> strings) {
        _id = id;
        _strings.clear();

        if (strings != null) {
            _strings.addAll(strings);
        }
    }

    /**
     * Returns the id this bundle has been created with.
     * <p>
     * @return [{@link String}]
     *     the bundle id
    */
    public String getId() {
        return _id;
    }

    /**
     * Returns a set of strings this bundle has been created with.
     * <p>
     * @return [{@link Set}<{@link String}>]
     *     the set of strings hosted by this bundle
    */
    public Set<String> getStrings() {
        return _strings;
    }

    @Override
    public boolean equals(final Object object) {
        return reflectionEquals(this, object);
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }
}
