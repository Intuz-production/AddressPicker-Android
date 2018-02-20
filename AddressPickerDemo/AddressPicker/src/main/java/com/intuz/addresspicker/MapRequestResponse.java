//  The MIT License (MIT)

//  Copyright (c) 2018 Intuz Solutions Pvt Ltd.

//  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
//  (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify,
//  merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:

//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
//  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
//  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
//  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


package com.intuz.addresspicker;

import java.util.ArrayList;

public class MapRequestResponse {
    private ArrayList<MapDataResult> results;
    private String status;

    public ArrayList<MapDataResult> getResults() {
        return results;
    }

    public void setResults(ArrayList<MapDataResult> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class MapDataResult {
        private ArrayList<AddressComponents> address_components;
        private String formatted_address;
        private Geometry geometry;

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }

        private String place_id;
        private ArrayList<String> types;

        public ArrayList<AddressComponents> getAddress_components() {
            return address_components;
        }

        public void setAddress_components(ArrayList<AddressComponents> address_components) {
            this.address_components = address_components;
        }

        public String getFormatted_address() {
            return formatted_address;
        }

        public void setFormatted_address(String formatted_address) {
            this.formatted_address = formatted_address;
        }



        public String getPlace_id() {
            return place_id;
        }

        public void setPlace_id(String place_id) {
            this.place_id = place_id;
        }

        public ArrayList<String> getTypes() {
            return types;
        }

        public void setTypes(ArrayList<String> types) {
            this.types = types;
        }

        public class AddressComponents {
            private String long_name;
            private String short_name;
            private ArrayList<String> types;


            public String getLong_name() {
                return long_name;
            }

            public void setLong_name(String long_name) {
                this.long_name = long_name;
            }

            public String getShort_name() {
                return short_name;
            }

            public void setShort_name(String short_name) {
                this.short_name = short_name;
            }

            public ArrayList<String> getTypes() {
                return types;
            }

            public void setTypes(ArrayList<String> types) {
                this.types = types;
            }
        }

        public class Geometry {

            private Location location;
            private String location_type;
            private  Viewport viewport;

            public Location getLocation() {
                return location;
            }

            public void setLocation(Location location) {
                this.location = location;
            }

            public String getLocation_type() {
                return location_type;
            }

            public void setLocation_type(String location_type) {
                this.location_type = location_type;
            }

            public Viewport getViewport() {
                return viewport;
            }

            public void setViewport(Viewport viewport) {
                this.viewport = viewport;
            }

            public class Location{
                private String lat;
                private String lng;

                public String getLat() {
                    return lat;
                }

                public void setLat(String lat) {
                    this.lat = lat;
                }

                public String getLng() {
                    return lng;
                }

                public void setLng(String lng) {
                    this.lng = lng;
                }
            }
            public class Viewport{
                private NorthEast northeast;
                private SouthWest southwest;

                public NorthEast getNortheast() {
                    return northeast;
                }

                public void setNortheast(NorthEast northeast) {
                    this.northeast = northeast;
                }

                public SouthWest getSouthwest() {
                    return southwest;
                }

                public void setSouthwest(SouthWest southwest) {
                    this.southwest = southwest;
                }

                public class NorthEast{
                    private String lat;
                    private String lng;

                    public String getLat() {
                        return lat;
                    }

                    public void setLat(String lat) {
                        this.lat = lat;
                    }

                    public String getLng() {
                        return lng;
                    }

                    public void setLng(String lng) {
                        this.lng = lng;
                    }
                }
                public class SouthWest{
                    private String lat;
                    private String lng;

                    public String getLat() {
                        return lat;
                    }

                    public void setLat(String lat) {
                        this.lat = lat;
                    }

                    public String getLng() {
                        return lng;
                    }

                    public void setLng(String lng) {
                        this.lng = lng;
                    }
                }


            }

        }
    }
}
