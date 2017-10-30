This is a porting of YamCha(some original features may be not supported). [Here](http://chasen.org/~taku/software/yamcha/) original link.

# TRAIN MODE
In this mode, jamcha creates its own model using feature tuning parameters and a train file.
### TRAIN FILE
- **LINE FORMAT**
  - Each line of train file contains a list of words separated by a white space character.
  - Every line must have same words count. Put `__nil__` as a placeholder: this will be skipped.  
- **TAG**
  - Last line word must be the `TAG`.
- **SENTENCE**
  - To separate two different sentences put an empty line between last line of previous sentence and first lline of next sentence.

### FEATURES TUNING PARAMETERS
Jamcha uses features tuning parameters to influence trainining and therefore model creation.
Read original YamCha [Re-definition of features (changing window-size)](http://chasen.org/~taku/software/yamcha/#tuning)

### USAGE: command line parameters
`train CORPUS=train_file_path MODEL=folder_path FEATURES="feature_tuning_parameters"`  
- **CORPUS**: path of training text file formatted as described above
- **MODEL**: folder path where the model created by jamcha will be saved. Jamcha will create its own folder.
- **FEATURES**: list of unordered features of type `F` and `T` (see features tuning parameters) separated by a white space character and enclosed between two `"`.  (e.g. FEATURES="F:-2..2:0..4 T:-5..-1 F:-3..0:1..3")

# PREDICT MODE
In this mode, jamcha try to predict `TAG` of each predict file line
### PREDICT FILE
Same as training file, but tag column may be present or not. If it is present, at the end of prediction, jamcha calculates and outputs how many `TAGs` have been correctly guessed.
### USAGE: command line parameters
`predict CORPUS=predict_file_path MODEL=folder_path` 
- **CORPUS**: path of predict text file formatted as described above
- **MODEL**: folder path where the model created by jamcha have been saved. You can put same folder path inserted in train mode
