(ns ft.slots)

;; array of tutors
;; array of their availabilities
;; array of scheduled speaking-session

;; Tutors:
;; {:id uuid
;;  :break int} (minutes)
(def t1 {:id #uuid"5357aab9-31fd-4b58-859c-e41c80a40a6b" :break 10})
(def t2 {:id #uuid"c198aa42-5679-4625-9659-943993ed01f1" :break 15})
(def tutors [t1 t2])

;; Availabilities:
;; {:id uuid
;;  :tutor-id uuid
;;  :start-at timestamp
;;  :end-at timestamp}

[;; Several examples of availability slots for the tutor `t1`.
 ;;
 ;; Tutor `t1` is available Jan 5th from 08:00 to 12:00...
 {:id       #uuid"fa24f7a1-0005-4497-b5e3-6e1568463bfb"
  :tutor-id (:id t1)
  :start-at (.getTime (js/Date. "2022-01-05 08:00"))
  :end-at   (.getTime (js/Date. "2022-01-05 12:00"))}

 ;; ...and `t1` is also available Jan 5th from 15:00 to 17:00
 ;; and that's it for `t1` for Jan 5th:
 {:id       #uuid"225b3fa6-6d0e-4137-b16c-6c54fc5b7fba"
  :tutor-id (:id t1)
  :start-at (.getTime (js/Date. "2022-01-05 15:00"))
  :end-at   (.getTime (js/Date. "2022-01-05 17:00"))}

 ;; Then, `t1` is available Jan 6th from 08:00 to 15:00 (only one period):
 {:id       #uuid"be52ea11-9b45-451b-800b-8d273f37340d"
  :tutor-id (:id t1)
  :start-at (.getTime (js/Date. "2022-01-06 08:00"))
  :end-at   (.getTime (js/Date. "2022-01-06 15:00"))}
 #_etc_for_every_tutor]

;; Speaking sessions:
;; {:id uuid
;;  :start-at timestamp
;;  :duration int
;;  :tutor-id uuid}
[{:id       #uuid"9121d3e6-1d5a-4aee-91f5-b90f14ead086"
  :start-at (.getTime (js/Date. "2022-01-03 09:00"))
  :duration 15
  :tutor-id (:id t1)}
 #_etc]

(defn available-slots
  [period-start period-end duration preferred-tutors])
