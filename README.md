# csv2beancount

A small utility to parse your .csv bank account transactions to beancount

## Usage

- Download the last version from github releases or download the code and run it with lein run
- Create a yml rules file. You have an example in resources/*.yml
- Execute the program with 2 parameters: `-c /path/to/your/transactions.csv -y /path/to/your/parsing_rules.yml`
- The program will output your beancount transactions. In case that you need to create a file you can redirect the output with `>` for example: `-c /path/to/your/transactions.csv -y /path/to/your/parsing_rules.yml > transactions.beancount`

## License

Copyright © 2017 Christian Panadero 

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
