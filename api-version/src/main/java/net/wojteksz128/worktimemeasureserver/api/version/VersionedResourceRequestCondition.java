package net.wojteksz128.worktimemeasureserver.api.version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VersionedResourceRequestCondition extends AbstractRequestCondition<VersionedResourceRequestCondition> {
    private final Logger logger = LoggerFactory.getLogger(VersionedResourceRequestCondition.class);
    private final Set<VersionRange> versions;
    private final String acceptedMediaType;

    public VersionedResourceRequestCondition(String acceptedMediaType, String from, String to) {
        this(acceptedMediaType, versionRange(from, to));
    }

    public VersionedResourceRequestCondition(String acceptedMediaType, Collection<VersionRange> versions) {
        this.acceptedMediaType = acceptedMediaType;
        this.versions = Set.copyOf(versions);
    }

    private static Set<VersionRange> versionRange(String from, String to) {
        Set<VersionRange> versionRanges = new HashSet<>();

        if (StringUtils.hasText(from)) {
            String toVersion = (StringUtils.hasText(to) ? to : Version.MAX_VERSION);
            VersionRange versionRange = new VersionRange(from, toVersion);

            versionRanges.add(versionRange);
        }
        return versionRanges;
    }

    @Override
    public VersionedResourceRequestCondition combine(VersionedResourceRequestCondition other) {
        logger.debug("Combining: \n{}\n{}", this, other);
        Set<VersionRange> newVersions = new LinkedHashSet<>(this.versions);
        newVersions.addAll(other.versions);
        String newMediaType;
        if (StringUtils.hasText(this.acceptedMediaType) && StringUtils.hasText(other.acceptedMediaType) && !this.acceptedMediaType.equals(other.acceptedMediaType)) {
            throw new IllegalArgumentException("Both conditions should have the same media type." + this.acceptedMediaType + " =!= " + other.acceptedMediaType);
        } else if (StringUtils.hasText(this.acceptedMediaType)) {
            newMediaType = this.acceptedMediaType;
        } else {
            newMediaType = other.acceptedMediaType;
        }
        return new VersionedResourceRequestCondition(newMediaType, newVersions);
    }

    @Override
    public VersionedResourceRequestCondition getMatchingCondition(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        Pattern regexPattern = Pattern.compile("(.*)-(\\d+\\.\\d+).*");
        Matcher matcher = regexPattern.matcher(accept);
        if (matcher.matches()) {
            String actualMediaType = matcher.group(1);
            String version = matcher.group(2);
            logger.debug("Version={}", version);

            if (acceptedMediaType.startsWith(actualMediaType)) {
                for (VersionRange versionRange : versions) {
                    if (versionRange.includes(version)) {
                        return this;
                    }
                }
            }
        }
        logger.debug("Didn't find matching version");
        return null;
    }

    @Override
    public int compareTo(VersionedResourceRequestCondition other, HttpServletRequest request) {
        return 0;
    }

    @Override
    protected Collection<?> getContent() {
        return versions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        VersionedResourceRequestCondition that = (VersionedResourceRequestCondition) o;

        if (!Objects.equals(versions, that.versions)) return false;
        return Objects.equals(acceptedMediaType, that.acceptedMediaType);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (versions != null ? versions.hashCode() : 0);
        result = 31 * result + (acceptedMediaType != null ? acceptedMediaType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "version={media=%s,%s}".formatted(acceptedMediaType, versions.stream().map(VersionRange::toString).collect(Collectors.joining(",")));
    }

    @Override
    protected String getToStringInfix() {
        return " && ";
    }
}
