(ns overtone-workshop.guitar
  (:use [overtone.live]
        [overtone.synth.stringed]))

(def g (guitar))

(comment
  (guitar-strum g :E :down 0.25)
  (guitar-strum g :E :up 0.75)
  (guitar-strum g :B :down 0.25)
  (guitar-strum g :A :up 0.5)
  (ctl g :pre-amp 2.0 :distort 0.85)
  (guitar-strum g [0 2 2 -1 -1 -1])
  (guitar-strum g [3 5 5 -1 -1 -1])
  (guitar-strum g [5 7 7 -1 -1 -1])
  (guitar-strum g :Gadd5 :down)
  ;; mute all strings
  (guitar-strum g [-1 -1 -1 -1 -1 -1]))

;; ======================================================================
;; try out a bit of rhythmic accompanyment
;; http://www.youtube.com/watch?v=DV1ANPOYuH8
;; http://www.guitar.gg/strumming.html
(defn pattern-to-beat-strum-seq
  "given a string describing a one-measure up/down strum pattern like
  'ud-udu-', return a sequence of vector [beats :up/:down] pairs"
  [cur-pattern]
  (let [strums-per-measure (count cur-pattern)
        beats-per-measure 4.0
        beats-per-strum (/ beats-per-measure strums-per-measure)
        ud-keywords {\u :up, \d :down}]
    (for [[i s] (map-indexed vector cur-pattern)]
      (when (contains? ud-keywords s)
        [(* i beats-per-strum) (ud-keywords s)]))))
(defn strum-pattern [the-guitar metro cur-measure cur-chord cur-pattern]
  (let [cur-beat (* 4 cur-measure)]
    (doall
     (doseq [[b d] (pattern-to-beat-strum-seq cur-pattern)]
       (when-not (= b nil)
         (guitar-strum the-guitar cur-chord d 0.07 (metro (+ b cur-beat))))))))

;; play a variety of different rhythm patterns.
(ctl g :pre-amp 10.0 :amp 1.0 :distort 0.0)

(comment ;; knocking on heaven's door
  (let [metro (metronome 100)]
    (doall
     (doseq [[i c] (map-indexed vector [:Gadd5 :Dsus4 :Am :Am
                                        :Gadd5 :Dsus4 :Am :Am
                                        :Gadd5 :Dsus4 :Cadd9 :Cadd9])]
       (strum-pattern g metro i c "d-du-udu")))))
(comment ;; 16th notes.
  (let [metro (metronome 90)]
    (doall
     (doseq [[i c] (map-indexed vector [:Gadd5 :Cadd9 :Gadd5 :Cadd9])]
       (strum-pattern g metro i c "d---d---dudu-ud-")))))

(defn ddd0 [g t]
  (let [dt 250]
    (guitar-strum g [-1  0  2  2  2 -1] :down 0.01 (+ t (* 0 dt)))
    (guitar-strum g [-1  0  2  2  2 -1] :up   0.01 (+ t (* 1 dt)))
    (guitar-strum g [-1  0  2  2  2 -1] :down 0.01 (+ t (* 2 dt) 50))
    (guitar-strum g [-1 -1 -1 -1 -1 -1] :down 0.01 (+ t (* 3.5 dt)))))
(defn ddd1 [g t]
  (let [dt 250]
    (guitar-strum g [ 2 -1  0  2  3 -1] :down 0.01 (+ t (* 0 dt)))
    (guitar-strum g [ 2 -1  0  2  3 -1] :up   0.01 (+ t (* 1 dt)))
    (guitar-strum g [ 3 -1  0  0  3 -1] :down 0.01 (+ t (* 2 dt) 50))
    (guitar-strum g [-1 -1 -1 -1 -1 -1] :down 0.01 (+ t (* 3.5 dt)))))
(defn ddd2 [g t]
  (let [dt 250]
    (guitar-strum g [ 2 -1  0  2  3 -1] :down 0.01 (+ t (* 0 dt)))
    (guitar-strum g [-1 -1 -1 -1 -1 -1] :down 0.01 (+ t (* 1.5 dt)))
    (guitar-strum g [-1  0  2  2  2 -1] :down 0.01 (+ t (* 2 dt)))
    (guitar-strum g [-1  0  2  2  2 -1] :up   0.01 (+ t (* 3 dt)))
    (guitar-strum g [-1 -1 -1 -1 -1 -1] :down 0.01 (+ t (* 4.5 dt)))))

(comment
  (let [g (guitar)]
    (ctl g :pre-amp 5.0 :distort 0.96
         :lp-freq 5000 :lp-rq 0.25
         :rvb-mix 0.5 :rvb-room 0.7 :rvb-damp 0.4)
    (ddd0 g (now)))
  (let [g (guitar) nome (metronome 116) beat (nome)]
    (ctl g :pre-amp 5.0 :distort 0.96
         :lp-freq 5000 :lp-rq 0.25
         :rvb-mix 0.5 :rvb-room 0.7 :rvb-damp 0.4)
    (apply-at (nome beat) #(ddd0 g (nome beat)))
    (apply-at (nome (+ 4 beat)) #(ddd1 g (nome (+ 4 beat))))
    (apply-at (nome (+ 8 beat)) #(ddd1 g (nome (+ 8 beat))))
    (apply-at (nome (+ 10 beat)) #(ddd1 g (nome (+ 10 beat))))
    (apply-at (nome (+ 12 beat)) #(ddd2 g (nome (+ 12 beat))))))

