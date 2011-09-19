package org.datacite.mds.web;

import org.datacite.mds.domain.Allocator;
import org.datacite.mds.domain.Datacentre;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.datacite.mds.domain.Prefix;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;

//@RooConversionService
//(disabled due to https://jira.springsource.org/browse/ROO-2593
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {
    
    @Override
    protected void installFormatters(FormatterRegistry registry) {
        super.installFormatters(registry);
        registry.addConverter(getByteArrayConverter());
        registry.addConverter(getSimpleAllocatorConverter());
        registry.addConverter(getSimpleDatacentreConverter());
        registry.addConverter(getSimpleDatasetConverter());
        registry.addConverter(getSimpleMetadataConverter());
        registry.addConverter(getSimplePrefixConverter());
    }

    public static Converter<byte[], String> getByteArrayConverter() {
        return new Converter<byte[], String>() {
            public String convert(byte[] bytes) {
                return new String(bytes);
            }
        };
    }
    
    public static Converter<Dataset, String> getSimpleDatasetConverter() {
        return new Converter<Dataset, String>() {
            public String convert(Dataset dataset) {
                return dataset.getDoi();
            }
        };
    }

    public static Converter<Datacentre, String> getSimpleDatacentreConverter() {
        return new Converter<Datacentre, String>() {
            public String convert(Datacentre datacentre) {
                return datacentre.getSymbol();
            }
        };
    }

    public static Converter<Allocator, String> getSimpleAllocatorConverter() {
        return new Converter<Allocator, String>() {
            public String convert(Allocator allocator) {
                return allocator.getSymbol();
            }
        };
    }

    public static Converter<Prefix, String> getSimplePrefixConverter() {
        return new Converter<Prefix, String>() {
            public String convert(Prefix prefix) {
                return prefix.getPrefix();
            }
        };
    }
    
    public static Converter<Metadata, String> getSimpleMetadataConverter() {
        return new Converter<Metadata, String>() {
            public String convert(Metadata metadata) {
                return metadata.getMetadataVersion() + " (" + metadata.getCreated() + ")" ;
            }
        };
    }
}
