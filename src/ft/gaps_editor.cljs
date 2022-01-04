(ns ft.gaps-editor
  (:require [reagent.core :as r]
            [clojure.string :as str]))

(def editor-content (r/atom {:phrase ""
                             :parts  []
                             :gaps   [:answer nil
                                      :variants '()]}))

(defn parse-phrase [phrase]
  "Example: 'Hello [World](Earth, Mars)!'."
  (swap! editor-content assoc :phrase phrase)

  (let [placeholders (re-seq #"\[(.+?)\]\((.+?)\)" (:phrase @editor-content))
        parts-without-gaps (str/split (:phrase @editor-content) #"\[.+?\)")
        parse-placeholder (fn [[_ answer wrong-variants]]
                            {:answer answer
                             :variants (map str/trim (str/split wrong-variants #","))})]
    (swap! editor-content assoc :parts (vec (map str/trim parts-without-gaps)))
    (swap! editor-content assoc :gaps (vec (map parse-placeholder placeholders)))))

(comment
  (parse-phrase (:phrase @editor-content))

  (re-find #"(.+?)\[(.+)\)(.+?)"
           (:phrase @editor-content)))

;(let [all (re-find #"\[(.+?)\]\((.+?)\)" (:phrase @editor-content))
;      [_ answer wrong-variants] all
;      variants (cons answer (map str/trim (str/split wrong-variants #",")))]
;  (prn all)
;
;  (swap! editor-content assoc :answer answer)
;  (swap! editor-content assoc :variants variants)))

(defn handle-select [ev]
  (let [start (.. ev -target -selectionStart)
        end (.. ev -target -selectionEnd)
        selection (subs (.. ev -target -value) start end)]
    (when (< 0 (count selection))
      (prn selection))))

(defn editor []
  [:<>
   [:div
    [:label.block "Enter the phrase with replacements:"]
    [:textarea.editor
     {:rows      10
      :cols      100
      :value     (:phrase @editor-content)
      :on-change #(parse-phrase (.. % -target -value))}]]])
    ;[:div.example [:small "E.g. `Hello [World](Earth, Mars)!`"]]]])

(defn strip-phrase [p]
  (let [with-dots (str/replace p #"\[.*?\)" "...")]
    [:p with-dots]))

(defn repr-gap [idx]
  (let [{:keys [:answer :variants]} (get (:gaps @editor-content) idx)]
    [:div.gap
     [:span.gap__index (inc idx)]
     [:ul.gap__dropdown
      (for [v (shuffle (conj variants answer))]
        [:li.gap__item v])]]))

(defn repr []
  (let [{:keys [parts gaps]} @editor-content]
    (prn (get gaps 0))
    [:<>
     [:div.out-arrow "↓"]
     [:pre.out (with-out-str (cljs.pprint/pprint @editor-content))]
     [:div.out-arrow "↓"]
     [:div.phrase
      (for [[idx part] (map-indexed (fn [idx itm] [idx itm]) parts)]
        [:span {:key idx}
          [:span part]
          (when (< (inc idx) (count parts)) ; TODO: make it better
            (repr-gap idx))])]]))
      ;(interleave parts gaps)]]))
;[:ul.variants
; (for [v (:variants @editor-content)]
;   [:li {:key v} v])]])
