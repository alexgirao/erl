#!/usr/bin/env escript
%% -*- erlang -*-

main(Args) ->
  Input = lists:nth(1, Args),
  Verify = Input ++ ".verify",
  case file:read_file(Input) of {ok, Data} -> Data end,
  Term = binary_to_term(Data),
  file:write_file(Verify, term_to_binary(Term, [{minor_version, 1}])),
  case file:read_file(Verify) of {ok, Data2} -> Data2 end,
  Data = Data2.
