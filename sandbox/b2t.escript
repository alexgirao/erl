#!/usr/bin/env escript
%% -*- erlang -*-

show_binary_term(FN) ->
    {ok, Data} = file:read_file(FN),
    io:format("read ~p~n~p~n", [FN, binary_to_term(Data)]).

main(_Args) ->
    show_binary_term("t2b.escript.bin"),
    show_binary_term("t2b.java.bin").
