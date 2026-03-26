package com.example.pr27_03;

import java.util.ArrayList;

public class AddressResponse {
    public class Response {
        public class GeoObjectCollection {
            public class FeatureMember{
                public class GeoObject {
                    public class MetaDataProperty {
                        public class GeocoderMetaData {
                            public String text;
                        }

                        public GeocoderMetaData GeocoderMetaData;
                    }
                    public MetaDataProperty metaDataProperty;
                }
                public GeoObject GeoObject;
            }
            public ArrayList<FeatureMember> featureMember;
        }
        public GeoObjectCollection GeoObjectCollection;
    }
    public Response response;
}
