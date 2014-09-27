(ns replme.web.resp)

(defn resp-ok
  [body & [content-type]]
  {:status 200
   :headers {"Content-Type" (or content-type "application/json")}
   :body body})

(defn resp-bad-request
  []
  {:status 400
   :headers {"Content-Type" "application/json"}
   :body {"status" "Bad Request"}})
