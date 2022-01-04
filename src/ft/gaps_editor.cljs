(ns ft.gaps-editor
  (:require [reagent.core :as r]
            [clojure.string :as str]))

(defonce exercise (r/atom {:phrase ""
                           :parts  []
                           :gaps   [:answer nil
                                    :variants '()]}))

(defonce ui (r/atom {:visible-dropdown-idx nil
                     :answers-highlighted? false
                     :data-repr-shown?     false}))

;; {0 "chosen answer", ...}
(defonce user-answers (r/atom {}))

(defn parse-phrase [phrase]
  (swap! exercise assoc :phrase phrase)
  (swap! ui assoc :visible-dropdown-idx nil :answers-highlighted? false)
  (reset! user-answers {})
  (let [parts-without-gaps (str/split (:phrase @exercise) #"\{.+?\}")
        gaps (re-seq #"\{(.+?)\}" (:phrase @exercise))
        parse-gap (fn [[_ variants]]
                    (reduce (fn [acc variant]
                              (if-let [[_ answer] (re-matches #"^\*(.+)\*$" variant)]
                                (assoc acc :answer answer)
                                (update acc :variants #(conj % variant))))
                            {:answer nil :variants '()}
                            (map str/trim (str/split variants #";"))))]
    (swap! exercise assoc :parts (vec (map str/trim parts-without-gaps)))
    (swap! exercise assoc :gaps (vec (map parse-gap gaps)))))

(defn editor []
  [:<>
   [:div.editor
    [:label "Enter the phrase with replacements:"]
    [:textarea.editor__area
     {:value     (:phrase @exercise)
      :on-change #(parse-phrase (.. % -target -value))}]
    [:small "List all answer variants inside " [:code "{...}"] " separated with " [:code ";"] ".
    Mark the right answer with " [:code "*...*"] ". E.g. " [:code "London is {*the*; a} capital of the UK."]]]])

(defn right-answer? [idx]
  (let [user-answer (get @user-answers idx)
        right-answer (:answer (get (:gaps @exercise) idx))]
    (= user-answer right-answer)))

(defn render-gap [idx]
  (let [{:keys [:answer :variants]} (get (:gaps @exercise) idx)
        toggle-dropdown (fn [idx]
                          (if (= idx (:visible-dropdown-idx @ui))
                            (swap! ui assoc :visible-dropdown-idx nil)
                            (swap! ui assoc :visible-dropdown-idx idx)))
        select-answer (fn [gap-idx user-answer]
                        (swap! ui assoc :answers-highlighted? false)
                        (swap! user-answers assoc gap-idx user-answer)
                        (toggle-dropdown nil))]
    [:div.gap
     [:span.gap__text
      {:on-click #(toggle-dropdown idx)
       :class    (when (:answers-highlighted? @ui)
                   (if (right-answer? idx)
                     "gap__text_right"
                     "gap__text_wrong"))}
      (cond
        (get @user-answers idx) (get @user-answers idx)
        (= idx (:visible-dropdown-idx @ui)) "×"
        :otherwise (inc idx))]
     [:ul.gap__dropdown
      {:class (when (= idx (:visible-dropdown-idx @ui)) "gap__dropdown_visible")}
      (for [v (shuffle (conj variants answer))]
        [:li.gap__item
         {:key      (str "test" v)
          :on-click #(select-answer idx v)}
         v])]]))

(defn check-user-answers []
  (swap! ui assoc :answers-highlighted? true))

(defn render-phrase []
  (let [{:keys [parts]} @exercise
        max-gap-idx (dec (count parts))]
    [:<>
     ;[:div.out-arrow "↓"]
     ;[:pre.out "@exercise\n\n" (with-out-str (cljs.pprint/pprint @exercise))]
     ;[:div.out-arrow "↓"]
     [:label "Choose the correct opiton:"]
     [:div.exercise
      (doall
        (for [[idx part] (map-indexed (fn [idx itm] [idx itm]) parts)]
          [:span {:key (str "phrase" idx)}
           [:span part]
           (when (< idx max-gap-idx)
             (render-gap idx))]))]
     ;[:pre.out "@user-answers\n\n" (with-out-str (cljs.pprint/pprint @user-answers))]
     [:div.actions
      [:button.btn
       {:disabled (not= (count @user-answers) (count (:gaps @exercise)))
        :on-click check-user-answers}
       "Check"]
      [:span.response (when (:answers-highlighted? @ui)
                        (if (every? right-answer? (keys @user-answers))
                          "✅ You're right!"
                          "🤔 Not exactly..."))]]
     [:hr]
     [:small.pseudo-link
      {:on-click #(swap! ui assoc :data-repr-shown? (not (:data-repr-shown? @ui)))}
      "Show data representation"]
     (when (:data-repr-shown? @ui)
       [:<>
         [:h3 "Data Representation"]
         [:p "Exercise:"]
         [:pre.out (str @exercise)]
         [:p "User answers:"]
         [:pre.out (str @user-answers)]])]))

