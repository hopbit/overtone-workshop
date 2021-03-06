(ns overtone-workshop.untztrument
  (:use [overtone.live]))

(defn prescale [value min max]
  (let [normalized-value (/ (- value 1) 127)]
    (+ min (* normalized-value (- max min)))))

(defn untztrument [synth-controls controls]
  (on-event [:midi :control-change]
            (fn [{value :velocity note :note}]
              (when-let [control (get @controls note)]
                (let [scaled-value (prescale value (:min control) (:max control))]
                  (swap! synth-controls assoc (:param control) scaled-value))))
            ::untztrument-control))

(defn untztrument-stop []
  (remove-event-handler ::untztrument-note))
