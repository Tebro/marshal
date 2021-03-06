(ns app.app
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [app.flights :as f]
            [app.data :as d]))

(defn stack-flight [existing name]
  (let [last-events (map (comp last val) existing)
        numbers (filter number? last-events)
        highest (apply max numbers)
        altitude (if highest (+ highest 1000) 2000)]
    (if (existing name)
      (conj (existing name) altitude)
      [altitude])))

(defn add-flight-form [add-flight-fn flights]
  (let [name (r/atom "")]
    (fn []
      (let [submit (fn []
                     (add-flight-fn @name)
                     (reset! name ""))]
        [:div
         [:input {:type "text"
                  :placeholder "Name (use , to add many together)"
                  :value @name
                  :on-change #(reset! name (.. % -target -value))
                  :on-key-down #(when (= 13 (.-keyCode %))
                                  (submit))}]
         [:button {:on-click submit} "Add"]
         [:br]
         [:select {:on-change #(reset! name (.. % -target -value))}
          [:option {:value ""} "-- select --"]
          (map
           (fn [[k _]]
             [:option {:key k} k])
           (filter (comp not number? last val) @flights))]]))))

(def flights (r/atom {}))

(defn send-charlie [name]
  (swap! flights (fn [old]
                   (let [old-alt (last (old name))
                         same-alt (map key (filter #(= (last (val %)) old-alt) old))
                         with-charlie (reduce #(update %1 %2 conj :charlie) old same-alt)]
                     (into {}
                           (map (fn [[k v]]
                                  [k (if (and (number? (last v))
                                              (> (last v) old-alt))
                                       (conj v (- (last v) 1000))
                                       v)])
                                with-charlie))))))

(defn set-landed [name]
  (swap! flights update name conj :landed))

(defn add-flight [name-input]
  (let [names (str/split name-input #",")
        infos (into {} (map (fn [n] [n (stack-flight @flights n)]) names))]
    (swap! flights merge infos)))

(defn change-altitude [change name]
  (let [new (+ change (last (@flights name)))]
    (swap! flights update name conj new)))

(def altitude-up (partial change-altitude 1000))
(def altitude-down (partial change-altitude -1000))

(defn app []
  [:div
   [:h1 "Marshal"]
   [add-flight-form add-flight flights]
   [f/flights-ui flights send-charlie set-landed altitude-up altitude-down]
   [d/import-export flights]])
