(ns app.app
  (:require [reagent.core :as r]
            [app.flights :as f]))

(defn create-flight [existing name]
  (let [existing-alts (map (comp last :alt val) existing)
        alt (max (+ (or (apply max existing-alts) 0) 1000) 2000)]
    {:name name
     :alt [alt]
     :charlie false
     :landed false
     :bolter 0
     :wo 0}))


(defn add-flight-form [add-flight-fn]
  (let [name (r/atom "")]
    [:div
     [:input {:type "text" :placeholder "Name" :on-change #(reset! name (-> % .-target .-value))}]
     [:button {:on-click #(add-flight-fn @name)} "Add"]]))


(def flights (r/atom {}))

()

(defn send-charlie [name]
  (swap! flights (fn [old]
                   (into {}
                         (map (fn [[k v]]
                                [k (update v :alt #(conj % (- (last %) 1000)))])
                              (update-in old [name :charlie] (fn [] true)))))))

(defn set-landed [name]
  (swap! flights update-in [name :landed] (fn [] true)))

(defn add-flight [name]
  (let [old-names (keys @flights)]
    (when (not ((set old-names) name))
      (let [info (create-flight @flights name)]
        (swap! flights assoc name info)))))

(defn app []
  [:div
   [:h1 "Marshal"]
   [add-flight-form add-flight]
   [f/flights-ui flights send-charlie set-landed]])
