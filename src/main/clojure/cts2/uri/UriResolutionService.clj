(ns cts2.uri
  (:require [clj-http.client :as client])
  (:import [java.net URLEncoder]))

(def call
  (memoize
   (fn [url response]
     (get
       (:body 
         (client/get url {:throw-exceptions false :accept :json :as :json :insecure? true})) (keyword response) ))))

(defn- doGetId 
 [baseuri type id resource]
    (call (str baseuri "/id/" type "?id=" (URLEncoder/encode id "UTF-8")) resource))

(defn getUri 
 [baseuri type id]
    (doGetId baseuri type id "resourceURI"))

(defn getBaseEntityUri 
 [baseuri id]
    (doGetId baseuri "CODE_SYSTEM" id "baseEntityURI"))

(defn getName 
 [baseuri type id]
    (doGetId baseuri type id "resourceName"))

(defn- doGetVersion
 [baseuri type identifier id resource]
    (call (str baseuri "/version/" type "/" identifier "/" id) resource))

(defn getVersionName 
  [url type identifier id]
    (doGetVersion url type identifier id "resourceName"))

(defn getVersionUri 
  [url type identifier id]
    (doGetVersion url type identifier id "resourceURI"))
