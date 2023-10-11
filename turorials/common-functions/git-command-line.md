## GIT

### 1. Git command line

- Change account information
```
$ git config --global user.name "taylq"
$ git config --global user.email "taylq@gemvietnam.com"
```

- Reset committed not push to repository
```
$ git reset HEAD~
```


- Fix Conflict
```
$ git checkout {branch}
$ git rebase {targetBranch}
  --> fix conflict ...
$ git add {fileName}
$ git rebase --continue
$ git push -f origin {branch}
```


### 2. Counting contributions to a git repo

- Get all commits
```
$ git shortlog -s --since="2023-09-20"
or
$ git rev-list HEAD --author="Macharia Maguta" --count --since="2023-09-20"
```

- Count total lines of code by author
```
$ git log --author="Macharia Maguta" --oneline --shortstat --count --since="2023-09-20" | grep 'insertions(+)' | awk -F ' ' '{ addition+=$4; remove+=$6 } END { print addition, remove; }' 
```

- Count total pull requests merged by author
```
$ git log --author="Macharia Maguta" --oneline --shortstat --count --since="2023-09-20" | grep "pull request" | awk '{ total+=1;} END { print total; }'
```


- Get log from date
```
$ git log --since="2023-09-20"
```
