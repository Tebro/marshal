(ns app.flights
  (:require [clojure.string :as str]))

(defn flights-ui [flights send-charlie trigger-landed]
  [:div
   [:div.stack
    [:h3 "Stack"]
    [:table
     [:thead
      [:tr
       [:th "Name"]
       [:th "Altitudes"]
       [:th "Charlie"]]]
     [:tbody
      (map (fn [[_ f]]
             (when (not (:charlie f))
               [:tr {:key (:name f)}
                [:td (:name f)]
                [:td (str/join "->" (:alt f))]
                [:td [:button {:on-click #(send-charlie (:name f))}
                      "Sent charlie"]]]))
           @flights)]]]
   [:div.approach
    [:h3 "On approach"]
    [:table
     [:thead [:tr 
              [:th "Name"]
              [:th "Land"]]]
     [:tbody 
      (map (fn [[_ f]]
             (when (and (:charlie f) (not (:landed f)))
               [:tr {:key (:name f)}
                [:td (:name f)]
                [:td [:button {:on-click #(trigger-landed (:name f))}
                      "Landed"]]])) 
           @flights)]]]])

