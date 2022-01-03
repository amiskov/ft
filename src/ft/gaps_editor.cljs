(ns ft.gaps-editor
  (:require [reagent.core :as r]
            [clojure.string :as str]))

(def state (r/atom {:phrase   "Hello [from](to, at) Gaps Editor!"
                    :answer   "from"
                    :variants '("from" "to" "at")}))

(defn parse-phrase [ev]
  "Example: 'Hello [World](Earth, Mars)!'."
  (swap! state assoc :phrase (.. ev -target -value))
  (let [[_ answer wrong-variants] (re-find #"\[(.*)\]\((.*)\)" (:phrase @state))
        variants (cons answer (map str/trim (str/split wrong-variants #",")))]
    (swap! state assoc :answer answer)
    (swap! state assoc :variants variants)))

(defn handle-select [ev]
  (let [start (.. ev -target -selectionStart)
        end (.. ev -target -selectionEnd)
        selection (subs (.. ev -target -value) start end)]
    (when (< 0 (count selection))
      (prn selection))))

(defn editor []
  [:<>
   ;[:textarea.editable
   ; { ;:content-editable                  true
   ;  :value "Hello World"
   ;  :on-select handle-select
   ;  :on-change #()}]
   ;  ;:suppress-content-editable-warning true} "Hello"]
   [:div
    [:label.block "Enter the phrase with replacements:"]
    [:textarea.editor
     {:rows      10
      :cols      100
      :value     (:phrase @state)
      :on-change parse-phrase}]
    [:div.example "E.g. `Hello [World](Earth, Mars)!`"]]])

(defn strip-phrase [p]
  (let [[before-gap after-gap] (str/split p #"\[.*\)")]
    [:p before-gap
     [:span.gap "..."]
     after-gap]))

(defn repr []
  [:<>
   [:div.phrase (strip-phrase (:phrase @state))]
   [:ul.variants
    (for [v (:variants @state)]
      [:li {:key v} v])]])
