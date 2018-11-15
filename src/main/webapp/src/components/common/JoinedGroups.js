import React from "react";
import List from "@material-ui/core/List/List";
import NetworkRequest from "../../util/NetworkRequest";
import CircularProgress from "@material-ui/core/CircularProgress/CircularProgress";
import ListItem from "@material-ui/core/ListItem/ListItem";
import ListItemText from "@material-ui/core/ListItemText/ListItemText";
import moment from "moment";

export default class JoinedGroups extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            data: undefined,
            interval: undefined
        };
    }

    componentDidMount() {
        this.getData();

        this.setState({
            interval: setInterval(this.getData, 1000)
        });
    }

    componentWillUnmount() {
        clearInterval(this.state.interval);
        this.setState({
            interval: undefined
        });
    }

    getData = async () => {
        try {
            const response = await NetworkRequest.get("/api/join-group");
            if (response.ok) {
                const data = await response.json();
                this.setState({data});
            }
            else {
                alert("Could not get joined groups");
            }
        }
        catch (exception) {
            console.error(exception);
        }
    };

    render() {
        if (this.state.data === undefined) {
            return <CircularProgress/>
        }
        return (
            <List component="nav">
                {
                    this.state.data.map((item, index) => (
                        <ListItem button key={index}>
                            <ListItemText
                                primary={`${item.ownerName}'s Group`}
                                secondary={`${moment(item.start).format("M/D, h:mma")} – ${moment(item.end).format("h:mma")}`}/>
                        </ListItem>
                    ))
                }
            </List>
        );
    }
}