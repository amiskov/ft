(ns ft.gaps-editor
  (:require [reagent.core :as r]
            [clojure.string :as str]))

(defonce exercise (r/atom {:phrase ""
                           :parts  []
                           :gaps   [:answer nil
                                    :variants '()]}))
(defonce ui (r/atom {:visible-dropdown-idx nil
                     :answers-highlighted? false}))

(defonce user-answers (r/atom {}))

(defn parse-phrase [phrase]
  (swap! exercise assoc :phrase phrase)
  (swap! ui assoc :visible-dropdown-idx nil :answers-highlighted? false)
  (reset! user-answers {})
  (prn @user-answers)
  (let [placeholders (re-seq #"\[(.+?)\]\((.+?)\)" (:phrase @exercise))
        parts-without-gaps (str/split (:phrase @exercise) #"\[.+?\)")
        parse-placeholder (fn [[_ answer wrong-variants]]
                            {:answer   answer
                             :variants (map str/trim (str/split wrong-variants #","))})]
    (swap! exercise assoc :parts (vec (map str/trim parts-without-gaps)))
    (swap! exercise assoc :gaps (vec (map parse-placeholder placeholders)))))

(defn handle-select [ev]
  (let [start (.. ev -target -selectionStart)
        end (.. ev -target -selectionEnd)
        selection (subs (.. ev -target -value) start end)]
    (when (< 0 (count selection))
      (prn selection))))

(defn editor []
  [:<>
   [:div.editor
    [:label "Enter the phrase with replacements:"]
    [:textarea.editor__area
     {:value     (:phrase @exercise)
      :on-change #(parse-phrase (.. % -target -value))}]
    [:small "List all possible answers inside " [:code "{...}"]
     ". Mark the right answer with " [:code "*...*"] ". E.g. " [:code "London is {*the*, a} capital of the UK."]]]])

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
                   (let [user-answer (get @user-answers idx)
                         right-answer (:answer (get (:gaps @exercise) idx))]
                     (if (= user-answer right-answer)
                       "gap__text_right"
                       "gap__text_wrong")))}
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
  (let [
        {:keys [parts]} @exercise]
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
           (when (< (inc idx) (count parts))                ; TODO: make it better
             (render-gap idx))]))]
     ;[:pre.out "@user-answers\n\n" (with-out-str (cljs.pprint/pprint @user-answers))]
     [:div.actions
      [:button.btn
       {:disabled (not= (count @user-answers) (count (:gaps @exercise)))
        :on-click check-user-answers}
       "Check"]]]))
